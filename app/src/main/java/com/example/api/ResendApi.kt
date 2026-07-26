package com.example.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ResendApi {
    @Headers("Content-Type: application/json")
    @POST("emails")
    suspend fun sendEmail(@retrofit2.http.Header("Authorization") authHeader: String, @Body emailRequest: EmailRequest): retrofit2.Response<Unit>
}

data class EmailRequest(
    val from: String = "onboarding@resend.dev",
    val to: String,
    val subject: String,
    val html: String
)
