package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.copotronic.stu.R
import com.copotronic.stu.helper.U
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this)
            .asGif()
            .load(R.drawable.splash)
            .into(ivSplash)


        Handler().postDelayed({
            goToDesiredPage()
            finish()
        }, 5000)




    }

    private fun goToDesiredPage() {
        startActivity(Intent(this, MainActivity::class.java))
    }


}
