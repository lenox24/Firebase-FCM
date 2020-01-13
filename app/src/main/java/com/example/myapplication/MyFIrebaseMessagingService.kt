package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFIrebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MsgService"

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "From: ${p0.from}")

        if (p0.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${p0.data}")
        }

        if (p0.notification != null) {
            Log.d(TAG, "Message Notification TItle: ${p0.notification!!.title}")
            Log.d(TAG, "Message Notification Body: ${p0.notification!!.body}")

            val messageTitle = p0.notification!!.title.toString()
            val messageBody = p0.notification!!.body.toString()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val notificationBuilder = NotificationCompat.Builder(this, "ddd")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
}