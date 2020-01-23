package com.example.myapplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R.drawable.ic_launcher_foreground
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.util.App
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFIrebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MsgService"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        Log.d(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        App.prefs.myToken = p0
        val intent = Intent("tokenData")
        intent.putExtra("token", p0)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "From: ${p0.from}")

        if (p0.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${p0.data}")
        }

        val messageTitle = p0.data["title"].toString()
        val messageBody = p0.data["body"].toString()
        /*val messageTitle = p0.notification!!.title.toString()
    val messageBody = p0.notification!!.body.toString()*/

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(this, "ddd")
            .setSmallIcon(ic_launcher_foreground)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "asdf"
            val channel =
                NotificationChannel("ddd", channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


}