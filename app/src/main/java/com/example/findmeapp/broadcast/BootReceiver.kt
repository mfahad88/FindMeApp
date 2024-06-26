package com.example.findmeapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.findmeapp.helper.Utils
import com.example.findmeapp.services.MyForegroundService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("BroadCast","Boot")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start your service here
            if(Utils.getService(context)=="1") {
                val serviceIntent = Intent(context, MyForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

            }
        }
    }
}