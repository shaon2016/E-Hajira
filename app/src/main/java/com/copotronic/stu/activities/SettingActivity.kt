package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.copotronic.stu.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        tvInstituteSetting.setOnClickListener {
            startActivity(Intent(this, InstituteSettingActivity::class.java))
        }
        tvListUser.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }
        tvUserManagement.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
    }
}
