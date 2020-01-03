package com.copotronic.stu.activities

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.coroutines.launch
import java.lang.Exception
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
            //            user = db.userDao().user(2)
            val userImage = BitmapFactory.decodeFile(user!!.imagePath)
            userImage
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userImage ->
                ivStudent.setImageBitmap(userImage)
            }, { it.printStackTrace() })


        Observable.fromCallable {
            val ut = db.userTypeDao().utById(user!!.userTypeId)
            ut
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ut ->
                tvAssignedFor.text = "Assigned for: ${ut.name}"

            }, { it.printStackTrace() })


        if (!user!!.name.isNullOrEmpty())
            tvName.text = "Name: ${user!!.name}"
        else tvName.text = "No name"
        if (!user!!.mobile.isNullOrEmpty())
            tvMobile.text = "Mobile: ${user!!.mobile}"
        else tvMobile.text = "No mobile number"
        if (!user!!.lineDescription.isNullOrEmpty())
            tvDesc.text = "Description: ${user!!.lineDescription}"
        else tvDesc.text = ""


        setDateTime()

        setNotice()
    }

    @SuppressLint("CheckResult")
    private fun setNotice() {
        Observable.fromCallable {
            db.noticeDao().noticeByUserTypeId(user!!.userTypeId)
        }.observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ notice ->
                if (notice != null && user!!.userTypeId > 0) {
                    tvNotice.text = notice.noticeText
                    Observable.fromCallable {
                        val myBitmap = BitmapFactory.decodeFile(notice.imageFilePath)
                        myBitmap
                    }.observeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { myBitmap ->
                            ivNotice.setImageBitmap(myBitmap)
                        }
                } else {
                    tvNotice.text = " No Notice to Show"
                    ivNotice.visibility = View.GONE
                }
            }, {
                tvNotice.text = " No Notice to Show"
                ivNotice.visibility = View.GONE
                it.printStackTrace()
            }, {})
    }

    private fun setDateTime() {
        tvDate.text = "${U.todayDate}"
        tvTime.text = "${U.nowTime}"
    }
}
