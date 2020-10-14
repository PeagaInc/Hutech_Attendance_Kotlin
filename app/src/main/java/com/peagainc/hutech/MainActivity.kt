package com.peagainc.hutech

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton

class MainActivity : AppCompatActivity() {
    private  lateinit var cirLoginPassworld: CircularProgressButton
    private  lateinit var  cirLoginBarScan: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        addEvent()
    }

    private fun initView() {
        cirLoginPassworld = findViewById(R.id.cirLoginPassworld)
        cirLoginBarScan = findViewById(R.id.cirLoginBarScan)
    }

    private fun addEvent() {
        cirLoginPassworld.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java).apply {  }
            startActivity(intent)
        }
        cirLoginBarScan.setOnClickListener{
//            val context:Context
//            val intent = Intent(this, LoginActivity::class.java).apply {  }
//            startActivity(intent)
                finish()
        }
    }
}