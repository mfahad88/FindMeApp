package com.example.findmeapp.broadcast

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.findmeapp.helper.AppDatabase
import com.example.findmeapp.helper.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                try {
                    val pdus = bundle["pdus"] as Array<*>
                    for (pdu in pdus) {
                        val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                        val sender = smsMessage.displayOriginatingAddress.replace("+92","0")
                        val messageBody = smsMessage.messageBody.lowercase()
                        val messageAddress = smsMessage.originatingAddress
                        checkDatabase(context,sender,messageBody,messageAddress)
                        /*if(sender==Utils.getNumber(context)){
                            if(messageBody.contains("loc!")){
                                Log.d("SmsBroadcastReceiver", "Sender: $sender, Message: $messageBody , Address $messageAddress")
                                fetchLocation(context)
                            }
                        }*/
                    }
                } catch (e: Exception) {
                    Log.e("SmsBroadcastReceiver", "Exception: $e")
                }
            }
        }
    }

    private fun checkDatabase(
        context: Context,
        sender: String,
        messageBody: String,
        messageAddress: String?
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            if(AppDatabase.getDatabase(context).userDao().getUser(sender)>0){
                if(messageBody.contains("loc!")){
                    Log.d("SmsBroadcastReceiver", "Sender: $sender, Message: $messageBody , Address $messageAddress")
                    fetchLocation(context)
                }
            }
        }

    }

    private fun fetchLocation(context: Context) {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setInterval(0)  // 10 seconds
            .setFastestInterval(0)



        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (isGooglePlayServicesAvailable(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location->
                val latitude = location.latitude
                val longitude = location.longitude
                Log.e("Location--->","Latitude: $latitude, Longitude: $longitude")
                Log.e("Location--->","http://maps.google.com/maps?q=$latitude,$longitude")
                sendSMS(Utils.getNumber(context).toString(),"This is my current location\nhttp://maps.google.com/maps?q=$latitude,$longitude")
            }


        } else {
            Toast.makeText(context, "Google Play Services not available", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)

        } catch (ex: java.lang.Exception) {

            ex.printStackTrace()
        }
    }
    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return status == ConnectionResult.SUCCESS
    }
}
