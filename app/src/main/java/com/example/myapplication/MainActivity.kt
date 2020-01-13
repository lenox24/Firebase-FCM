package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.w("FCM Log", "getInstanceId failed", it.exception)
                    return@addOnCompleteListener
                }
                val token = it.result!!.token
                Log.d("FCM Log", "FCM 토큰: $token")

                Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            }
    }
}
