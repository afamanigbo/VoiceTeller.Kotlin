package com.aiconverse.voiceteller

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.util.PreferencesManager
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

class AuthActivity : AppCompatActivity() {

    private lateinit var progress: ProgressBar

    private val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private lateinit var user: FirebaseUser
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        progress = findViewById(R.id.progress)

        //PreferencesManager.getInstance(applicationContext).clearAllItems()
        //firebaseAuth.signOut()

        if(firebaseAuth.currentUser == null){
            createSignInIntent()
        }
        else{
            handleSignIn(firebaseAuth.currentUser!!, null)
        }

    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {

            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            handleSignIn(user!!, result)
        }

        else {

            // user cancelled sign in
            if (response == null){
                Toast.makeText(applicationContext, "Sign in cancelled.", Toast.LENGTH_LONG).show()
            }
            else{
                Timber.d("M: Sign in failed: ${response.error?.errorCode}")
                Toast.makeText(applicationContext, "Sign in failed.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleSignIn(user: FirebaseUser, result: FirebaseAuthUIAuthenticationResult?){

        // get logged in user
        user?.let {
            // get user from Firebase Auth
            val profileModel = ProfileModel(
                id = user.email!!,
                names = user.displayName!!,
                authenticationType = "firebase",
                identityProvider = user.providerData[0].providerId,
                createDate = user.metadata?.creationTimestamp.toString(),
                imageUrl = user.photoUrl.toString(),
                walletModel = null
            )

            // get user from shared preferences
            var sharedPreferencesProfileModel = viewModel
                .getUserSharedPreferences(profileModel.id, applicationContext)

            // if user does not exist there, create it
            if (sharedPreferencesProfileModel == null) {

                Timber.d("M: Shared preferences NULL")

                viewModel.createUserSharedPreferences(profileModel, applicationContext)

                // user must have manually signed in for shared preferences to be null

                // Not a new user
                if (!result?.idpResponse?.isNewUser!!) {

                    Timber.d("M: OLD user")

                    // try to sync from cloud, this will also internally set cloud status
                    user?.getIdToken(false)?.addOnSuccessListener { rsp ->
                        val jwt = rsp.token.toString()
                        viewModel.getUserWorker(jwt, profileModel.id)
                    }
                }
                else{
                    // This is a new user

                    Timber.d("M: NEW user")

                    // DIFF is true, something has changed - (New User sign in)
                    viewModel.setDiffStatus(user.email!!, "PUSH", applicationContext)

                    // not in cloud, so push from device to cloud
                    user?.getIdToken(false)?.addOnSuccessListener { rsp ->
                        val jwt = rsp.token.toString()
                        viewModel.createUserWorker(jwt, profileModel)
                    }

                }
            }

            // User are on shared preferences. user has signed in before
            else {

                Timber.d("M: Shared preferences NOT NULL")

                val cloudStatus =
                    viewModel.getUserCloudStatus(profileModel.id, applicationContext)

                Timber.d("M: Cloud Status: $cloudStatus")

                when (cloudStatus) {
                    "404" -> {
                        // not in cloud, so push from device to cloud
                        user?.getIdToken(false)?.addOnSuccessListener { rsp ->
                            val jwt = rsp.token.toString()
                            viewModel.createUserWorker(jwt, profileModel)
                        }
                    }
                    "200" -> {
                        // downloaded from cloud to device initially
                        // so push from device to cloud
                        user?.getIdToken(false)?.addOnSuccessListener { rsp ->
                            val jwt = rsp.token.toString()
                            viewModel.createUserWorker(jwt, profileModel)
                        }
                    }
                    else -> {
                        // cannot determine status, sync from cloud
                        user?.getIdToken(false)?.addOnSuccessListener { rsp ->
                            val jwt = rsp.token.toString()
                            viewModel.getUserWorker(jwt, profileModel.id)
                        }
                    }
                }
            }

        }

        // if email not verified, send verification email
        // else redirect to MainActivity

        navigateFromActivity()

    }

    private fun navigateFromActivity(){

        val user = FirebaseAuth.getInstance().currentUser

        val newActivity = when (user?.isEmailVerified) {
            true -> MainActivity::class.java
            else -> VerifyEmailActivity::class.java
        }

        val intent = Intent(this, newActivity)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()

    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        val customLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_auth_firebase)
            .setEmailButtonId(R.id.cmdEmail)
            .setGoogleButtonId(R.id.cmdGoogle)
            .setFacebookButtonId(R.id.cmdFacebook)
            .setTwitterButtonId(R.id.cmdTwitter)
            .build()

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAuthMethodPickerLayout(customLayout)
            .build()

        signInLauncher.launch(signInIntent)
    }
}