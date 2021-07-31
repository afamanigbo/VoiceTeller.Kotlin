package com.aiconverse.voiceteller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.ui.wallet.WalletViewModel
import com.aiconverse.voiceteller.util.PreferencesManager
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import timber.log.Timber

class AuthActivity : AppCompatActivity() {

    private lateinit var progress: ProgressBar

    private val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private lateinit var user: FirebaseUser
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener {

        if (it.currentUser != null) {
            user = it.currentUser!!

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
        else{
            createSignInIntent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        progress = findViewById(R.id.progress)

        //PreferencesManager.getInstance(applicationContext).clearAllItems()

        firebaseAuth.signOut()
        //firebaseAuth!!.addAuthStateListener(this.authStateListener!!)
        createSignInIntent()
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

            // get JWT
            user?.getIdToken(false)?.addOnSuccessListener { rsp ->

                val jwt = rsp.token.toString()

                // get user from Firebase Auth
                val profileModel = ProfileModel(
                    id = user?.email!!,
                    names = user?.displayName!!,
                    authenticationType = "firebase",
                    identityProvider = response?.providerType!!,
                    createDate = user?.metadata?.creationTimestamp.toString(),
                    imageUrl = user?.photoUrl.toString(),
                    walletModel = null
                )

                // get user from shared preferences
                var sharedPreferencesProfileModel = viewModel
                    .getUserSharedPreferences(profileModel.id, applicationContext)

                // if user does not exist there, create it
                if (sharedPreferencesProfileModel == null) {

                    viewModel.createUserSharedPreferences(profileModel, applicationContext)

                    // not a new firebase user
                    if (!result?.idpResponse?.isNewUser!!) {

                        // try to sync from cloud, this will also internally set cloud status
                        viewModel.getUserWorker(jwt, profileModel.id)
                    }
                    else{
                        // This is a new user
                        // ensure its pushed to cloud later,

                        viewModel.createUserWorker(jwt, profileModel)
                    }

                }
                else {
                    // user is on shared preferences
                    val cloudStatus =
                        viewModel.getUserCloudStatus(profileModel.id, applicationContext)

                    Timber.d("Cloud Status: $cloudStatus")

                    when (cloudStatus) {
                        "404" -> {
                            // not in cloud, so push from device to cloud
                            viewModel.createUserWorker(jwt, profileModel)
                        }
                        "200" -> {
                            // downloaded from cloud to device initially
                            // so push from device to cloud
                            viewModel.createUserWorker(jwt, profileModel)
                        }
                        else -> {
                            // cannot determine status, sync from cloud
                            viewModel.getUserWorker(jwt, profileModel.id)
                        }
                    }
                }
            }

            // if email not verified, send verification email
            // else redirect to MainActivity

            navigateFromActivity()
        }

        else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
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