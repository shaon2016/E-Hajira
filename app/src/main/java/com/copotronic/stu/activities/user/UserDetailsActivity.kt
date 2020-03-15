package com.copotronic.stu.activities.user

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_details.*
import java.util.*

class UserDetailsActivity : AppCompatActivity() {
    private var user: User? = null
    private lateinit var db: AppDb

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        db = AppDb.getInstance(this)!!

        user = intent?.extras?.getSerializable("user") as User


        Observable.fromCallable {
            val userImage = BitmapFactory.decodeFile(user!!.imagePath)
            userImage
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userImage ->
                ivStudent.setImageBitmap(userImage)
            }, { it.printStackTrace() })


//        Observable.fromCallable {
//            val ut = db.userTypeDao().utById(user!!.userTypeId)
//            ut
//        }.subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ ut ->
//                tvAssignedFor.text = "Assigned for: ${ut.name}"
//
//            }, { it.printStackTrace() })


        if (!user!!.name.isNullOrEmpty())
            tvName.text = "Name: ${user!!.name}"
        else tvName.text = "No name"

        if (!user!!.name.isNullOrEmpty())
            tvUserId.text = "ID- ${user!!.userId}"
        else tvUserId.visibility = View.GONE

        if (user!!.designationId > 0)
            Observable.fromCallable {
                db.designationDao().desgById(user!!.designationId)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tvDesignation.text = "Designation: ${it.name}"
                }, { it.printStackTrace() })
        else tvDesignation.visibility = View.GONE

        setDateTime()

        setNotice()

        // After 5 seconds finish this page
        Handler().postDelayed({
            finish()
        }, 5000)
    }

    @SuppressLint("CheckResult")
    private fun setNotice() {
        Observable.fromCallable {
                val todayDate = U.reformatDate(Calendar.getInstance().time, "yyyy-MM-dd")
                db.noticeDao().noticeByUserTypeId(user!!.userTypeId, todayDate)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ notice ->
                if (notice != null && user!!.userTypeId > 0) {
                    if (notice.noticeText.isNotEmpty()) {
                        tvNotice.visibility = View.VISIBLE
                        tvNotice.text = notice.noticeText
                    }
                    Observable.fromCallable {
                        val myBitmap = BitmapFactory.decodeFile(notice.imageFilePath)
                        myBitmap
                    }.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ myBitmap ->
                            ivNotice.setImageBitmap(myBitmap)
                        }, { it.printStackTrace() })
                } else {
                    tvNotice.text = ""
                    ivNotice.visibility = View.GONE
                }
            }, {
                tvNotice.text = ""
                ivNotice.visibility = View.GONE
                it.printStackTrace()
            }, {})
    }

    private fun setDateTime() {
        tvDate.text = "Date: ${U.todayDate}"
        tvTime.text = "Time: ${U.nowTime}"
    }
}
