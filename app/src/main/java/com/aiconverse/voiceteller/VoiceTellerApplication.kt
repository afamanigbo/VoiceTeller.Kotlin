package com.aiconverse.voiceteller

import android.app.Application
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.interswitchng.iswmobilesdk.IswMobileSdk
import com.interswitchng.iswmobilesdk.shared.models.core.IswSdkConfig
import timber.log.Timber

class VoiceTellerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // update play services if needed
        updateAndroidSecurityProvider()

        // configure sdk
        configureSDK()
    }

    private fun configureSDK(){
        val merchantId: String = BuildConfig.MERCHANT_ID
        val merchantCode: String = BuildConfig.MERCHANT_CODE
        val merchantKey: String = BuildConfig.MERCHANT_KEY

        val config = IswSdkConfig(merchantId, merchantKey, merchantCode, "566")

        // uncomment to set environment, default is Environment.TEST
        // config.setEnv(Environment.SANDBOX);

        IswMobileSdk.initialize(this, config)
    }

    // for devices below android lollipop
    private fun updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            // GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}