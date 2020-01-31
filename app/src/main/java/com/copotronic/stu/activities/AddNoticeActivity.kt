package com.copotronic.stu.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.copotronic.stu.helper.D
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.Notice
import com.copotronic.stu.model.UserType
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_notice.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class AddNoticeActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private var typeId = 0
    /** Request code for gallery image selection for ad post*/
    private val REQUEST_GALLERY_IMAGE = 231
    private var image: Image? = null
    private var noticeText = ""
    private var noticeDate = ""

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
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            ImagePicker.create(this@AddNoticeActivity)
                                .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
                                .toolbarImageTitle(getString(R.string.tap_to_select)) // image selection title
                                .toolbarArrowColor(Color.BLACK)
                                .limit(1)
                                .showCamera(true)
                                .toolbarArrowColor(ContextCompat.getColor(this@AddNoticeActivity, R.color.white))
                                .start(REQUEST_GALLERY_IMAGE)
                        }else
                            D.showToastLong(this@AddNoticeActivity, "Plz Grant the permissions")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()


        }

        btnAddDate.setOnClickListener {
            setDate()
        }

        btnSubmit.setOnClickListener {
            save()
        }
    }

    private fun setDate() {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener
            { pickerView, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)

                noticeDate = U.reformatDate(cal.time, "yyyy-MM-dd")
                this@AddNoticeActivity.tvNoticeDate.post {
                    tvNoticeDate.text = noticeDate
                }
            },
            y, m, d
        ).show()
    }

    private fun save() {
        if (noticeDate.isEmpty()) {
            D.showToastShort(this, "Please add date")
            return
        }


        noticeText = evNotice.text.toString()
        val notice = Notice(0, image?.path ?: "", typeId, noticeText, noticeDate)

        image?.let {
            copyFileToDestination()
        }

        Thread {
            db.noticeDao().insert(notice)
        }.start()

        D.showToastShort(this, "Notice saved")
        finish()
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

            this.image = image
            tvImageNoticeName.text = image?.name

            showUserImage()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("CheckResult")
    private fun showUserImage() {
        Observable.fromCallable {
            //            user = db.userDao().user(2)
            val userImage = BitmapFactory.decodeFile(image?.path)
            userImage
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userImage ->
                ivNotice.setImageBitmap(userImage)
            }, { it.printStackTrace() })
    }

    private fun copyFileToDestination() {
        val calender = Calendar.getInstance()

        val src = File(image!!.path)
        val destination = File(
            getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), "${calender.timeInMillis}${image?.name}"
        )

        Thread {
            U.copyOrMoveFile(src, destination, true)
        }.start()

    }


}
