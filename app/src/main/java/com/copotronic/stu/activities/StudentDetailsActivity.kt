package com.copotronic.stu.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.User
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.coroutines.launch
import java.lang.Exception

class StudentDetailsActivity : AppCompatActivity() {
    private var user: User? = null
    private lateinit var db: AppDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        db = AppDb.getInstance(this)!!

        user = intent?.extras?.getSerializable("user") as User

        val userImage = BitmapFactory.decodeFile(user!!.imagePath)
        ivStudent.setImageBitmap(userImage)


        try {
            val ut = db.userTypeDao().utById(user!!.userTypeId)
            tvAssignedFor.text = "Assigned for: ${ut.name}"
        }catch (e : Exception) {
            e.printStackTrace()
        }


    }
}
