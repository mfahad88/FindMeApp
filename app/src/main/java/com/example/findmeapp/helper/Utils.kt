package com.example.findmeapp.helper

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData


object Utils {
    fun saveSharedPreferences(context: Context,key:String,number: String){
        val sh: SharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        sh.edit().putString(key,number).apply()
    }
    fun deleteNumber(context: Context) {
        val sh: SharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        return sh.edit().remove(AppConstant.NUMBER).apply()
    }

    fun getNumber(context: Context): String? {

        val sh: SharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        return sh.getString(AppConstant.NUMBER,"")

    }

    fun getService(context: Context): String? {
        val sh: SharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        return sh.getString(AppConstant.SERVICE,"")
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}