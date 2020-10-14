package com.peagainc.hutech

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var cirLoginButton: CircularProgressButton;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        try {
            val requireNonNull = Objects.requireNonNull(this.supportActionBar)
            requireNonNull?.hide()
        } catch (ignored: NullPointerException) {
        }
        initView()
        addEvent()
    }

    private fun initView() {
        cirLoginButton = findViewById(R.id.cirLoginButton)
    }


    private fun addEvent() {
        cirLoginButton.setOnClickListener{
            val context:Context
            val done_ic :Bitmap =BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp)
            cirLoginButton.startAnimation()
            cirLoginButton.setProgress(100)
            cirLoginButton.doneLoadingAnimation(R.color.colorAccent,done_ic)
            val intent = Intent(this, HomeActivity::class.java).apply {  }
            startActivity(intent)
            finish()
        }
    }

}