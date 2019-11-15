package com.copotronic.stu.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.UserType
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_add_notice.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList


class AddNoticeActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private var typeId = 0
    /** Request code for gallery image selection for ad post*/
    private val REQUEST_GALLERY_IMAGE = 231

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notice)

        initVar()
        initView()
    }

    private fun initVar() {
        db = AppDb.getInstance(this)!!
    }

    private fun initView() {
        setUserTypes()

        btnAddImageNotice.setOnClickListener {
            ImagePicker.create(this)
                .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
                .toolbarImageTitle(getString(R.string.tap_to_select)) // image selection title
                .toolbarArrowColor(Color.BLACK)
                .limit(1)
                .showCamera(true)
                .toolbarArrowColor(ContextCompat.getColor(this, R.color.white))
                .start(REQUEST_GALLERY_IMAGE)
        }
    }

    private fun setUserTypes() {
        db.userTypeDao().all().observe(this, Observer { types ->
            types as ArrayList<UserType>
            types.add(0, UserType(0, getString(R.string.select_type)))

            if (types.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, types
                )
                spinUserType.adapter = adapter

                spinUserType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        typeId = types[position].id
                    }
                }
            } else spinUserType.visibility = View.GONE
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            val image = ImagePicker.getFirstImageOrNull(data)

            copyFileToDestination(image)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun copyFileToDestination(image: Image) {
        val calender = Calendar.getInstance()

        val src = File(image.path)
        val destination = File(
            getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), "${calender.timeInMillis}${image.name}"
        )

        Thread {
            U.copyOrMoveFile(src, destination, true)
        }.start()
    }


}
