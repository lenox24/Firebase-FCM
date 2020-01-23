package com.example.myapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R
import com.example.myapplication.connector.Connector
import com.example.myapplication.util.App
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
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

    private var topics = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val str = App.prefs.myTopics
        str.let {
            try {
                val a = JSONArray(str)
                for (i in 0 until a.length()) {
                    val topic = a.optString(i)
                    topics.add(topic)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topics)
        listView.adapter = adapter

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

                //Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            }


        listView.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)

            builder.setTitle("구독 취소")
                .setMessage(topics[position] + "의 알림 구독을 취소하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    onSubscribe(topics[position], "unsub")
                }
                .setNegativeButton("취소") { _, _ ->
                }.setCancelable(false).show()
        }

        btn_agree.setOnClickListener {
            /*val token = App.prefs.myToken
            updateAgree("true", token)*/
            onSubscribe(editText.text.toString(), "sub")
        }

        fun updateAgree(agree: String, token: String) {
            val reqJson = JsonObject()

            reqJson.addProperty("token", token)
            reqJson.addProperty("agree", agree)

            Connector.createApi().updateAgree(reqJson).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {

                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                }

            })
        }
    }

    fun showDialog(token: String) {
        val obj = JsonObject()
        obj.addProperty("type", "android")
        obj.addProperty("token", token)

        val builder = AlertDialog.Builder(this)

        builder.setTitle("알림 수신 동의")
            .setMessage("알림 수신에 동의하시겠습니까?")
            .setPositiveButton("동의") { _, _ ->
                /* obj.addProperty("agree", "true")
                 sendToken(obj)*/
                onSubscribe("All", "sub")
            }
            .setNegativeButton("취소") { _, _ ->
                /*obj.addProperty("agree", "false")
                sendToken(obj)*/
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
        val a = JSONArray()
        for (i in 0 until topics.size) {
            a.put(topics[i])
        }
        App.prefs.myTopics = a.toString()
    }

    fun onSubscribe(topic: String, subscribe: String) {
        val messaging = FirebaseMessaging.getInstance()
        when (subscribe) {
            "sub" -> {
                if (!topics.contains(topic))
                    messaging.subscribeToTopic(topic).addOnCompleteListener {
                        var msg = "Subscribe Complete: $topic"
                        if (!it.isSuccessful)
                            msg = "Subscribe Failed: $topic"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        topics.add(topic)
                        adapter.notifyDataSetChanged()
                    }
            }
            "unsub" -> {
                if (topics.contains(topic))
                    messaging.unsubscribeFromTopic(topic).addOnCompleteListener {
                        var msg = "unSubscribe Complete: $topic"
                        if (!it.isSuccessful)
                            msg = "unSubscribe Failed: $topic"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        topics.remove(topic)
                        adapter.notifyDataSetChanged()
                    }
            }
        }
    }
}
