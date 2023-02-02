package com.cookandroid.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn : Button = findViewById(R.id.startBtn)

        startBtn.setOnClickListener({
            val intent = Intent(this, Chatting::class.java)        // 채팅 화면으로 넘어가기
            startActivity(intent)
        })


    }
}