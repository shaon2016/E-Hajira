package com.copotronic.stu.activities

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.coroutines.launch
import java.lang.Exception

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


    }
}
