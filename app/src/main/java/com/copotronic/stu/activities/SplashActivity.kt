package com.copotronic.stu.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.C
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.Notice
import com.copotronic.stu.model.UserType
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
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


       // setHomeUserType()

    }

    @SuppressLint("CheckResult")
    private fun setHomeUserType() {
        Observable.fromCallable {
            val userTypeDao = AppDb.getInstance(this)?.userTypeDao()

            val homeExistType = userTypeDao?.utById(1)

            if (homeExistType == null) {
                val homeType = UserType(C.home)
                userTypeDao?.insert(homeType)

            }
        }.subscribeOn(Schedulers.io())
            .subscribe({}, { it.printStackTrace() })
    }

    private fun goToDesiredPage() {
        startActivity(Intent(this, MainActivity::class.java))
    }


}
