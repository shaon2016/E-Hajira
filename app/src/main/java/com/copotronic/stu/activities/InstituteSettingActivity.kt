package com.copotronic.stu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.D
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.*
import kotlinx.android.synthetic.main.activity_institute_setting.*

class InstituteSettingActivity : AppCompatActivity() {

    private lateinit var db: AppDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_institute_setting)

        initVar()
        initView()
    }

    private fun initVar() {
        db = AppDb.getInstance(this)!!
    }

    private fun initView() {
        handleBtn()

        setDesignation()
        setDept()
        setSection()
        setUserType()
        setShift()
    }

    private fun setDesignation() {
        db.designationDao().all().observe(this, Observer { desgs ->
            fblDesignation.removeAllViews()
            desgs.forEach { desg ->
                val tv = TextView(this)
                tv.text = desg.name
                tv.textSize = 16f
                fblDesignation.addView(tv)
            }
        })
    }

    private fun setDept() {
        db.deptDao().all().observe(this, Observer { depts ->
            fblDepartment.removeAllViews()
            depts.forEach { dept ->
                val tv = TextView(this)
                tv.text = dept.name
                tv.textSize = 16f
                fblDepartment.addView(tv)
            }
        })
    }
    private fun setSection() {
        db.sectionDao().all().observe(this, Observer { sects ->
            fblSection.removeAllViews()
            sects.forEach { sec ->
                val tv = TextView(this)
                tv.text = sec.name
                tv.textSize = 16f
                fblSection.addView(tv)
            }
        })
    }
    private fun setShift() {
        db.shiftDao().all().observe(this, Observer { shifts ->
            fblShift.removeAllViews()
            shifts.forEach { s ->
                val tv = TextView(this)
                tv.text = s.name
                tv.textSize = 16f
                fblShift.addView(tv)
            }
        })
    }
    private fun setUserType() {
        db.userTypeDao().all().observe(this, Observer { uts ->
            fblUserType.removeAllViews()
            uts.forEach { ut ->
                val tv = TextView(this)
                tv.text = ut.name
                tv.textSize = 16f
                fblUserType.addView(tv)
            }
        })
    }

    private fun handleBtn() {
        btnAddUserType.setOnClickListener {
            addUserType()
        }

        btnAddDepartment.setOnClickListener {
            addDepartment()
        }
        btnAddDesignation.setOnClickListener {
            addDesignation()
        }
        btnAddSection.setOnClickListener {
            addSection()
        }
        btnAddShift.setOnClickListener {
            addShift()
        }

        btnDone.setOnClickListener {
            saveInstituteValue()
        }
    }

    private fun saveInstituteValue() {
        val insId = evInstituteId.text.toString()
        val name = evInstituteName.text.toString()
        val address = evInstituteAddress.text.toString()
        val mobileNo = evInstituteMobileNo.text.toString()
        val deviceId = evInstituteDeviceId.text.toString()

        if (insId.isNullOrEmpty()) {
            D.showToastShort(this, "Insert institute id")
            return
        }
        if (name.isNullOrEmpty()) {
            D.showToastShort(this, "Insert institute name")
            return
        }
        if (address.isNullOrEmpty()) {
            D.showToastShort(this, "Insert institute address")
            return
        }
        if (mobileNo.isNullOrEmpty()) {
            D.showToastShort(this, "Insert institute mobile no")
            return
        }
        if (deviceId.isNullOrEmpty()) {
            D.showToastShort(this, "Insert institute device id")
            return
        }
        Thread {
            val inst = Institute(0, insId, name, address, 0.0, 0.0, U.todayDate)

            db.instituteDao().insert(inst)
        }.start()

        D.showToastLong(this, "Institute Details saved successfully")
        finish()
    }

    private fun addShift() {
        MaterialDialog(this).show {
            message(null, "Add a new shift")
            input(hint = "shift") { dialog, charSequence ->
                val sStr = charSequence.toString()
                if (sStr.isNotEmpty()) {
                    Thread {
                        val sht = Shift(0, sStr)
                        db.shiftDao().insert(sht)
                    }.start()
                }
            }
            positiveButton {
                dismiss()
            }
        }
    }

    private fun addSection() {
        MaterialDialog(this).show {
            message(null, "Add a new section")
            input(hint = "section") { dialog, charSequence ->
                val secStr = charSequence.toString()
                if (secStr.isNotEmpty()) {
                    Thread {
                        val sec = Section(0, secStr)
                        db.sectionDao().insert(sec)
                    }.start()
                }
            }
            positiveButton {
                dismiss()
            }
        }
    }

    private fun addDesignation() {
        MaterialDialog(this).show {
            message(null, "Add a new designation")
            input(hint = "designation") { dialog, charSequence ->
                val desStr = charSequence.toString()
                if (desStr.isNotEmpty()) {
                    Thread {
                        val des = Designation(0, desStr)
                        db.designationDao().insert(des)
                    }.start()
                }
            }
            positiveButton {
                dismiss()
            }
        }
    }

    private fun addDepartment() {
        MaterialDialog(this).show {
            message(null, "Add a new department")
            input(hint = "department") { dialog, charSequence ->
                val deptStr = charSequence.toString()
                if (deptStr.isNotEmpty()) {
                    Thread {
                        val dept = Department(0, deptStr)
                        db.deptDao().insert(dept)
                    }.start()
                }
            }
            positiveButton {
                dismiss()
            }
        }
    }

    private fun addUserType() {
        MaterialDialog(this).show {
            message(null, "Add a new user type")
            input(hint = "user type") { dialog, charSequence ->
                val userTypeStr = charSequence.toString()
                if (userTypeStr.isNotEmpty()) {
                    Thread {
                        val ut = UserType( userTypeStr)
                        db.userTypeDao().insert(ut)
                    }.start()
                }
            }
            positiveButton {
                dismiss()
            }
        }
    }
}
