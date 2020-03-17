package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.copotronic.stu.R
import com.copotronic.stu.activities.notice.ListOfNoticeActivity
import com.copotronic.stu.activities.user.AddUserActivity
import com.copotronic.stu.activities.user.UsersActivity
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.D
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    private lateinit var db: AppDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        db = AppDb.getInstance(this)!!

        tvInstituteSetting.setOnClickListener {
            startActivity(Intent(this, InstituteSettingActivity::class.java))
        }
        tvListUser.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }
        tvUserManagement.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
        tvNoticeManagement.setOnClickListener {
            startActivity(Intent(this, ListOfNoticeActivity::class.java))
        }

        tvChangePassword.setOnClickListener {
            changePassword()
        }
        tvAbout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun changePassword() {
        MaterialDialog(this).show {
            title = "Change password"
            customView(R.layout.dialog_change_password)
            val v = getCustomView()

            v.setPadding(16, 16, 16, 0)


            val evOldPass = v.findViewById<EditText>(R.id.evOldPassword)
            val evNewPassword = v.findViewById<EditText>(R.id.evNewPassword)
            val evConfirmNewPassword = v.findViewById<EditText>(R.id.evConfrimNewPassword)
            val btnChange = v.findViewById<Button>(R.id.btnChange)
            val btnCancel = v.findViewById<Button>(R.id.btnCancel)

            val user = db.userDao().user(1)

            btnChange.setOnClickListener {
                if (user != null) {
                    val oPass = evOldPass.text.toString()

                    if (oPass.isEmpty() || user.pinNo.isNullOrEmpty() || oPass != user.pinNo) {
                        D.showToastLong(this@SettingActivity, "Old password is wrong")
                        return@setOnClickListener
                    }

                    val newPass = evNewPassword.text.toString()
                    val newConfirmPass = evConfirmNewPassword.text.toString()

                    if (newPass.isEmpty() || newPass.isEmpty() || newPass != newConfirmPass) {
                        D.showToastLong(this@SettingActivity, "Password mismatch")
                        return@setOnClickListener
                    }

                    user.pinNo = newConfirmPass
                    db.userDao().insert(user)

                    D.showToastLong(this@SettingActivity, "Password successfully changed")
                    dismiss()
                } else {
                    D.showToastLong(this@SettingActivity, "User not exist")
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}
