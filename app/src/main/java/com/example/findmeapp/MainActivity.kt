package com.example.findmeapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.findmeapp.databinding.ActivityMainBinding
import com.example.findmeapp.helper.AppConstant
import com.example.findmeapp.helper.Utils
import com.example.findmeapp.services.MyForegroundService


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    companion object {
        private const val SMS_PERMISSION_CODE = 101
        private const val LOCATION_PERMISSION_CODE = 102
    }

    override fun onResume() {
        super.onResume()
        if( Utils.getService(this)=="1"){
            binding.buttonStart.isEnabled=false
        }else{
            binding.buttonStart.isEnabled=true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestPermissions()
        addTracker()

        if(Utils.getNumber(this)?.isNotEmpty() == true){
            binding.textViewNumber.text=Utils.getNumber(this)
        }

       if( Utils.getService(this)=="1"){
           binding.buttonStart.isEnabled=false
       }else{
           binding.buttonStart.isEnabled=true
       }



        binding.buttonStart.setOnClickListener {
            startService()
        }

        binding.buttonStop.setOnClickListener {
            stopService()
        }
    }

    override fun onDestroy() {
      /*  Utils.saveSharedPreferences(this,AppConstant.SERVICE,"0")
        binding.buttonStop.performClick()*/
        super.onDestroy()

    }

    private fun stopService() {
        val intent=Intent(this@MainActivity,MyForegroundService::class.java)
        stopService(intent)

        binding.buttonStart.isEnabled=true
    }

    private fun startService() {
        val intent= Intent(this@MainActivity,MyForegroundService::class.java)
        startService(intent)

        binding.buttonStart.isEnabled=false

    }

    private fun checkAndRequestPermissions() {
        val writesmsPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)
        val readsmsPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS)
        val receivesmsPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        val listPermissionsNeeded = mutableListOf<String>()

        if (writesmsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (readsmsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS)
        }
        if (receivesmsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), SMS_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION_CODE -> {
                val smsPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val fineLocationPermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val coarseLocationPermissionGranted = grantResults[2] == PackageManager.PERMISSION_GRANTED

                if (smsPermissionGranted && fineLocationPermissionGranted && coarseLocationPermissionGranted) {
                    // All permissions are granted, proceed with the functionality that requires these permissions
                    // e.g., send SMS or access GPS location
                    addTracker()
                } else {
                    // Permissions are denied, show a message to the user
                    Toast.makeText(this, "Permissions are required for this app to function", Toast.LENGTH_SHORT).show()
                    finish()
                    System.exit(0)

                }
            }
        }
    }

    private fun addTracker() {
        binding.btnAdd.setOnClickListener {
            Utils.hideKeyboardFrom(this@MainActivity,binding.btnAdd)
            if(binding.edtPhone.text.isNotEmpty() && binding.edtPhone.text.length==11){
                Utils.saveSharedPreferences(this@MainActivity,AppConstant.NUMBER,binding.edtPhone.text.toString())
                if(Utils.getNumber(this)?.isNotEmpty() == true){
                    binding.textViewNumber.text=Utils.getNumber(this)
                }
            }
        }


    }
}