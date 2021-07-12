package com.aiconverse.voiceteller

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
import com.facebook.AccessToken
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

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
            // ...

            //val authProvider = response?.providerType + ": " + user?.email + " " + user?.displayName
            //Toast.makeText(this,authProvider,Toast.LENGTH_LONG).show()

            //FirebaseAuth.getInstance().currentUser.photoUrl + AccessToken.getCurrentAccessToken()

//            user?.photoUrl?.let{
//                Toast.makeText(this, it.toString() ,Toast.LENGTH_LONG).show()
//            }

            if(response?.providerType == "password"){
                if(! user?.isEmailVerified !!) {
                    if (response?.isNewUser!!) {
                        user?.sendEmailVerification()
                    }
                    // redirect to email confirmation activity
                }
            }


            //if(result.idpResponse?.isNewUser()){
            //    user?.sendEmailVerification()
            //}
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_signout]
    }

    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
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
        // [END auth_fui_create_intent]
    }

}