package com.example.myapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R
import com.example.myapplication.connector.Connector
import com.example.myapplication.util.App
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val token = intent?.getStringExtra("token").toString()

            showDialog(token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = App.prefs.myAgree

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mBroadcastReceiver, IntentFilter("tokenData"))

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.w("FCM Log", "getInstanceId failed", it.exception)
                    return@addOnCompleteListener
                }
                val token = it.result!!.token
                Log.d("Token", "Success to Send Token1")
                Log.d("FCM Log", "FCM 토큰: $token")

                Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            }


        btn_agree.setOnClickListener {
            val token = App.prefs.myToken
            updateAgree("true", token)
            textView.text = "알림 수신 동의"
        }

        btn_disagree.setOnClickListener {
            val token = App.prefs.myToken
            updateAgree("false", token)
            textView.text = "알림 수신 거부"
        }
    }

    fun updateAgree(agree: String, token: String) {
        val reqJson: JsonObject = JsonObject()

        reqJson.addProperty("token", token)
        reqJson.addProperty("agree", agree)

        Connector.createApi().updateAgree(reqJson).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {

            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }

        })
    }

    fun showDialog(token: String) {
        val obj = JsonObject()
        obj.addProperty("type", "android")
        obj.addProperty("token", token)

        val builder = AlertDialog.Builder(this)

        builder.setTitle("알림 수신 동의")
            .setMessage("알림 수신에 동의하시겠습니까?")
            .setPositiveButton("동의") { _, _ ->
                obj.addProperty("agree", "true")
                sendToken(obj)
                textView.text = "알림 수신 동의"
            }
            .setNegativeButton("취소") { _, _ ->
                obj.addProperty("agree", "false")
                sendToken(obj)
                textView.text = "알림 수신 거부"
            }.setCancelable(false).show()
    }

    fun sendToken(obj: JsonObject) {
        Connector.createApi().sendToken(obj).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Token", "Failed to Send Token")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.code() == 200)
                    Log.d("Token", "Success to Send Token")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        App.prefs.myAgree = textView.text.toString()
    }
}
