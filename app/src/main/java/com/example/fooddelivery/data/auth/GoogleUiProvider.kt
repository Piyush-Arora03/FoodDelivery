package com.example.fooddelivery.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.example.fooddelivery.GoogleServiceClientId
import com.example.fooddelivery.data.modle.GoogleAccount
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleUiProvider {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInWithGoogle(
        activityContext: Context,
        credentialManager: CredentialManager
    ): GoogleAccount? {
        return try {
            val request = getCredentialRequest()
            val response: GetCredentialResponse = withContext(Dispatchers.IO) {
                credentialManager.getCredential(activityContext, request)
            }
            handleGoogleResponseCredentials(response)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun handleGoogleResponseCredentials(response: GetCredentialResponse): GoogleAccount {
        val credential = response.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // ✅ Correct way to create GoogleIdTokenCredential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val name = googleIdTokenCredential.displayName ?: "Unknown"
            val token = googleIdTokenCredential.idToken
            val profileImageUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""

            return GoogleAccount(
                token,
                name,
                profileImageUrl
            )
        } else {
            throw IllegalStateException("Unknown credential type received")
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(GoogleServiceClientId).build()
            )
            .build()
    }
}
