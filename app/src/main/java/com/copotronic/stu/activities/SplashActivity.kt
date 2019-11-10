package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.copotronic.stu.MainActivity
import com.copotronic.stu.R
import com.copotronic.stu.helper.U
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            setDateTime()
            goToDesiredPage()
        }, 2000)
    }

    private fun goToDesiredPage() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun setDateTime() {
        tvDate.text = "${U.todayDate}"
        tvTime.text = "${U.nowTime}"
    }
}
