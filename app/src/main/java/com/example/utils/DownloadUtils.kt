package com.example.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

suspend fun downloadAudio(context: Context, url: String, title: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDescription("Downloading audio...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setMimeType("audio/mpeg")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.mp3")
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    
    val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }
    if (authToken != null) {
        request.addRequestHeader("Authorization", "Bearer $authToken")
    }
    
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}
