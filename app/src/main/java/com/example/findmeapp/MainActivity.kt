package com.example.findmeapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmeapp.adapter.UserAdapter
import com.example.findmeapp.databinding.ActivityMainBinding
import com.example.findmeapp.helper.AppConstant
import com.example.findmeapp.helper.AppDatabase
import com.example.findmeapp.helper.Utils
import com.example.findmeapp.model.User
import com.example.findmeapp.services.MyForegroundService
import com.example.findmeapp.viewmodel.UserViewModel


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    companion object {
        private const val MULTIPLE_PERMISSIONS_REQUEST_CODE = 1
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
        initRecycler()
        userViewModel.mutableLiveData.observe(this, Observer { users->
            (binding.recyclerView.adapter as UserAdapter).addItem(users)
        })
        if(Utils.getNumber(this)?.isNotEmpty() == true){
//            binding.textViewNumber.text=Utils.getNumber(this)
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

    private fun initRecycler() {
        binding.recyclerView.apply {
            layoutManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
            adapter = UserAdapter()
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
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_SMS)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS_REQUEST_CODE)
        } else {
            // All permissions are already granted
            Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSIONS_REQUEST_CODE -> {
                val permissionsGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (permissionsGranted) {
                    addTracker()
                } else {
                    Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show()
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
                userViewModel.insertUser(this@MainActivity,
                    User(number = binding.edtPhone.text.toString())
                )
                binding.edtPhone.text.clear()

            }
        }


    }
}