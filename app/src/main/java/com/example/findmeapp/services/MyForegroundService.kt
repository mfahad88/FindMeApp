package com.example.findmeapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.findmeapp.MainActivity
import com.example.findmeapp.R
import com.example.findmeapp.broadcast.SmsBroadcastReceiver
import com.example.findmeapp.helper.AppConstant
import com.example.findmeapp.helper.Utils

class MyForegroundService : Service() {
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        unregisterReceiver(smsBroadcastReceiver)
        super.onDestroy()
    }


    override fun onCreate() {
        super.onCreate()
        Utils.saveSharedPreferences(this, AppConstant.SERVICE,"1")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service is running")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your service logic here
//        readSMS()
        smsBroadcastReceiver = SmsBroadcastReceiver()

        // Register the receiver
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsBroadcastReceiver, intentFilter)
        return START_STICKY
    }

    private fun readSMS() {
        val uri: Uri = Uri.parse("content://sms/inbox")
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                Log.d("SMS", "ID: $id, Address: $address, Body: $body")
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun stopService(name: Intent?): Boolean {
        unregisterReceiver(smsBroadcastReceiver)
        Utils.saveSharedPreferences(this,AppConstant.SERVICE,"0")
        return super.stopService(name)
    }
}
