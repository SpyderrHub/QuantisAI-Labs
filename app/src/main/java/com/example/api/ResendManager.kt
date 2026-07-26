package com.example.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ResendManager {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.resend.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build())
        .build()

    private val resendApi = retrofit.create(ResendApi::class.java)

    suspend fun sendOtpEmail(email: String, otp: String, fromEmail: String = "QuantisAI Labs <no-reply@quantisai.org>"): Boolean {
        RemoteConfigManager.initialize()
        val apiKey = RemoteConfigManager.getApiKey("RESEND_API_KEY")
        Log.d("ResendManager", "API Key fetched: ${apiKey.take(5)}... Length: ${apiKey.length}")
        if (apiKey.isEmpty()) {
            Log.e("ResendManager", "API Key is empty")
            return false
        }
        
        val emailRequest = EmailRequest(
            from = fromEmail,
            to = email,
            subject = "Verify your QuantisAI Labs account",
            html = """
            <div style="font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 40px; background-color: #ffffff; border-radius: 24px; border: 1px solid #e2e8f0;">
        <div style="text-align: center; margin-bottom: 32px;">
          <h1 style="margin: 0; color: #FF6600; font-size: 28px; font-weight: 800; letter-spacing: -0.025em;">QuantisAI Labs</h1>
          <p style="margin-top: 8px; color: #64748b; font-size: 14px; font-weight: 500; text-transform: uppercase; letter-spacing: 0.1em;">🎉 Congratulations! Your account is almost ready</p>
        </div>
        
        <div style="margin-bottom: 32px; color: #1e293b;">
          <p style="font-size: 16px; line-height: 1.6; margin-bottom: 24px;">Welcome to QuantisAI Labs. To activate your account and start generating studio-quality voices, please verify your email address using the code below:</p>
          
          <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 32px; text-align: center; margin-bottom: 24px;">
            <span style="font-family: 'Courier New', Courier, monospace; font-size: 42px; font-weight: 900; letter-spacing: 0.2em; color: #1e293b;">$otp</span>
          </div>
          
          <p style="font-size: 14px; color: #64748b; font-style: italic; text-align: center;">This code will expire in 10 minutes for your security.</p>
        </div>
        
        <div style="border-top: 1px solid #f1f5f9; padding-top: 24px; margin-top: 32px; padding-top: 24px; color: #94a3b8; font-size: 12px; text-align: center; line-height: 1.5;">
          <p style="margin: 0;">If you didn't create an account with QuantisAI Labs, you can safely ignore this email.</p>
          <p style="margin-top: 8px;">&copy; ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} QuantisAI Labs Systems. All rights reserved.</p>
        </div>
      </div>
            """
        )

        return try {
            val response = resendApi.sendEmail("Bearer $apiKey", emailRequest)
            if (!response.isSuccessful) {
                Log.e("ResendManager", "Failed to send email: ${response.code()} ${response.message()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ResendManager", "Error sending email", e)
            false
        }
    }
}
