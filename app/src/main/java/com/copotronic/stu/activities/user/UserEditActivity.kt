package com.copotronic.stu.activities.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.copotronic.stu.R
import com.copotronic.stu.ScannerAction
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.D
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.*
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.mantra.mfs100.FingerData
import com.mantra.mfs100.MFS100
import com.mantra.mfs100.MFS100Event
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_edit.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserEditActivity : AppCompatActivity(), MFS100Event {

    private lateinit var db: AppDb
    private var desgId = 0
    private var secId = 0
    private var shiftId = 0
    private var deptId = 0
    private var typeId = 0

    // Finger print related variable instances
    private lateinit var mfs100: MFS100
    private var mLastClkTime: Long = 0
    private val Threshold: Long = 1500
    private var mLastAttTime = 0L
    private var timeout = 10000
    private var leftCapFingerData: FingerData? = null
    private var rightCapFingerData: FingerData? = null
    private var scannerAction = ScannerAction.Capture
    private var leftEnrollTemplate: ByteArray? = null
    private var rightEnrollTemplate: ByteArray? = null
    private var leftVerifyTemplate: ByteArray? = null
    private var rightVerifyTemplate: ByteArray? = null

//    private var leftFingerFingerImageDataInStr: String? = null
//    private var rightFingerFingerImageDataInStr: String? = null
//    private var leftFingerFingerImageDataInByteArray: ByteArray? = null
//    private var rightFingerFingerImageDataInByteArray: ByteArray? = null
//    private var leftFingerISOTemplateDataInStr: String? = null
//    private var rightFingerISOTemplateDataInStr: String? = null

    // User image
    /** Request code for gallery image selection for ad post*/
    private val REQUEST_GALLERY_IMAGE = 231
    private var image: Image? = null

    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit)



        initVar()
        initView()
    }

    private fun initVar() {
        user = intent?.extras?.getSerializable("user") as User
        db = AppDb.getInstance(this)!!

        initMFS100()

//        leftFingerFingerImageDataInStr = user.leftFingerDataBase64
//        rightFingerFingerImageDataInStr = user.rightFingerDataBase64
//        leftFingerISOTemplateDataInStr = user.leftFingerISOTemplateDataBase64
//        rightFingerISOTemplateDataInStr = user.rightFingerISOTemplateDataBase64
//        leftFingerFingerImageDataInByteArray = user.leftFingerISOTemplateDataByteArray
//        rightFingerFingerImageDataInByteArray = user.rightFingerISOTemplateDataByteArray


    }

    private fun initView() {
        setUserValueInEV()
        setUserImage()

        setLineDescription()
        setDesignation()
        setDepts()
        setSection()
        setShifts()
        setUserTypes()


        btnSubmit.setOnClickListener {
            update()
        }

        btnStartCaptureLeftFinger.setOnClickListener {
            scannerAction = ScannerAction.Capture
            startSyncLeftFingerCapture()
        }
        btnStartCaptureRightFinger.setOnClickListener {
            scannerAction = ScannerAction.Capture
            startSyncRightFingerCapture()
        }

        btnAddUserImage.setOnClickListener {
            ImagePicker.create(this)
                .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
                .toolbarImageTitle(getString(R.string.tap_to_select)) // image selection title
                .toolbarArrowColor(Color.BLACK)
                .limit(1)
                .showCamera(true)
                .toolbarArrowColor(ContextCompat.getColor(this, R.color.white))
                .start(REQUEST_GALLERY_IMAGE)
        }

        btnVerifyCaptureLeftFinger.setOnClickListener {
            scannerAction = ScannerAction.Verify
            if (!isCaptureRunning) {
                startSyncLeftFingerCapture()
            }
        }
        btnVerifyCaptureRightFinger.setOnClickListener {
            scannerAction = ScannerAction.Verify
            if (!isCaptureRunning) {
                startSyncRightFingerCapture()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setUserImage() {
        Observable.fromCallable {
            val userImage = BitmapFactory.decodeFile(user.imagePath)
            userImage
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userImage ->
                ivUser.setImageBitmap(userImage)
            }, { it.printStackTrace() })
    }

    private fun initMFS100() {
        try {
            mfs100 = MFS100(this)
            mfs100.SetApplicationContext(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun setUserValueInEV() {
        evUserId.setText(user.userId)
        evName.setText(user.name)
        evMobile.setText(user.mobile)
        evPin.setText(user.pinNo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            val image = ImagePicker.getFirstImageOrNull(data)

            this.image = image

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
                ivUser.setImageBitmap(userImage)
            }, { it.printStackTrace() })
    }

    // Saving the new image
    // deleting the previous image
    @SuppressLint("CheckResult")
    private fun copyFileToDestination() {

    }

    @SuppressLint("CheckResult")
    private fun startUpdating() {
        Log.d("DATATAG", "Called 3")

        Observable.fromCallable {
            val user = User(
                user.id, user.userId, user.name, user.mobile, user.userTypeId, user.designationId,
                user.departmentId, user.sectionId, user.shiftId, user.lineDescriptionId, user.pinNo,
                user.imagePath,  user.leftFingerDataBase64,
                user.rightFingerDataBase64,
                user.leftFingerISOTemplateDataBase64, user.rightFingerISOTemplateDataBase64,
                user.leftFingerISOTemplateDataByteArray, user.rightFingerISOTemplateDataByteArray
            )
            Log.d("DATATAG", user.toString())
            db.userDao().insert(user)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("DATATAG", "Called 4")
                D.showToastShort(this, "User updated successfully")
                finish()
            }, { it.printStackTrace() })
    }

    @SuppressLint("CheckResult")
    private fun update() {
        user.name = evName.text.toString()
        user.userId = evUserId.text.toString()
        user.mobile = evMobile.text.toString()
        user.pinNo = evPin.text.toString()


        if (user.pinNo.isNullOrEmpty()) {
            D.showToastShort(this, "Insert user pin")
            return
        }
        if (user.userId.isNullOrEmpty()) {
            D.showToastShort(this, "Insert user id")
            return
        }
        if (user.name.isNullOrEmpty()) {
            D.showToastShort(this, "Insert name")
            return
        }
        if (user.mobile.isNullOrEmpty()) {
            D.showToastShort(this, "Insert mobile no")
            return
        }

//        if (user.leftFingerDataBase64.isNullOrEmpty()) {
//            D.showToastShort(this, "Left finger data is missing")
//            return
//        }
//        if (user.rightFingerDataBase64.isNullOrEmpty()) {
//            D.showToastShort(this, "Right finger data is missing")
//            return
//        }

        if (image != null) {
            Observable.fromCallable {
                val calender = Calendar.getInstance()

                val src = File(image!!.path)
                val destination = File(
                    getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                    ), "${calender.timeInMillis}${image?.name}"
                )

                U.copyOrMoveFile(src, destination, true)

                destination
            }.subscribeOn(Schedulers.io())
                .subscribe({ destinationFile ->

                    U.deleteAFile(user.imagePath)
                    user.imagePath = destinationFile.absolutePath
                    startUpdating()
                }, { it.printStackTrace() })
        } else startUpdating()


    }

    private fun setDesignation() {
        db.designationDao().all().observe(this, Observer { desgs ->
            desgs as ArrayList<Designation>
            desgs.add(0, Designation(0, getString(R.string.select_designation)))

            if (desgs.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, desgs
                )

                spinUserDesignation.adapter = adapter

                (desgs.indices).forEach { i ->
                    val d = desgs[i]

                    if (d.id == user.designationId) {
                        spinUserDesignation.setSelection(i)
                    }
                }

                spinUserDesignation.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            user.designationId = desgs[position].id
                        }
                    }
            } else spinUserDesignation.visibility = View.GONE
        })
    }
    private fun setLineDescription() {
        db.lineDescriptionDao().all().observe(this, Observer { desc ->
            desc as ArrayList<LineDescription>
            desc.add(0, LineDescription(0, getString(R.string.select_line_description)))

            if (desc.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, desc
                )
                spinLineDescription.adapter = adapter

                (desc.indices).forEach { i ->
                    val d = desc[i]

                    if (d.id == user.designationId) {
                        spinUserDesignation.setSelection(i)
                    }
                }

                spinLineDescription.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            user.lineDescriptionId = desc[position].id
                        }
                    }
            } else spinLineDescription.visibility = View.GONE
        })
    }

    private fun setSection() {
        db.sectionDao().all().observe(this, Observer { secs ->
            secs as ArrayList<Section>
            secs.add(0, Section(0, getString(R.string.select_sction)))

            if (secs.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, secs
                )
                spinSection.adapter = adapter

                (secs.indices).forEach { i ->
                    val d = secs[i]

                    if (d.id == user.sectionId) {
                        spinSection.setSelection(i)
                    }
                }

                spinSection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        user.sectionId = secs[position].id
                    }
                }
            } else spinSection.visibility = View.GONE
        })
    }

    private fun setDepts() {
        db.deptDao().all().observe(this, Observer { depts ->
            depts as ArrayList<Department>
            depts.add(0, Department(0, getString(R.string.select_department)))

            if (depts.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, depts
                )
                spinUserDepartment.adapter = adapter

                (depts.indices).forEach { i ->
                    val d = depts[i]

                    if (d.id == user.departmentId) {
                        spinUserDepartment.setSelection(i)
                    }
                }

                spinUserDepartment.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            user.departmentId = depts[position].id
                        }
                    }
            } else spinUserDepartment.visibility = View.GONE
        })
    }

    private fun setShifts() {
        db.shiftDao().all().observe(this, Observer { shifts ->
            shifts as ArrayList<Shift>
            shifts.add(0, Shift(0, getString(R.string.select_shift)))

            if (shifts.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, shifts
                )
                spinUserShift.adapter = adapter

                (shifts.indices).forEach { i ->
                    val d = shifts[i]

                    if (d.id == user.shiftId) {
                        spinUserShift.setSelection(i)
                    }
                }

                spinUserShift.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        user.shiftId = shifts[position].id
                    }
                }
            } else spinUserShift.visibility = View.GONE
        })
    }

    private fun setUserTypes() {
        db.userTypeDao().all().observe(this, Observer { types ->
            types as ArrayList<UserType>
            types.add(0, UserType( getString(R.string.select_type)))

            if (types.size > 1) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, types
                )
                spinUserType.adapter = adapter

                (types.indices).forEach { i ->
                    val d = types[i]

                    if (d.id == user.userTypeId) {
                        spinUserType.setSelection(i)
                    }
                }

                spinUserType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        user.userTypeId = types[position].id
                    }
                }
            } else spinUserType.visibility = View.GONE
        })
    }

    private fun showSuccessLog(key: String) {
        try {
            setFingerPrintDeviceTextOnUIThread("Device is ready")
            val info = ("\nKey: " + key + "\nSerial: "
                    + mfs100.GetDeviceInfo().SerialNo() + " Make: "
                    + mfs100.GetDeviceInfo().Make() + " Model: "
                    + mfs100.GetDeviceInfo().Model()
                    + "\nCertificate: " + mfs100.GetCertification())
            showLonOnLogcat(info)
        } catch (e: Exception) {
        }

    }

    private fun showLonOnLogcat(str: String) {

        Log.d("DATATAG", str)
    }

    override fun OnDeviceAttached(vid: Int, pid: Int, hasPermission: Boolean) {
        if (SystemClock.elapsedRealtime() - mLastAttTime < Threshold) {
            return
        }
        mLastAttTime = SystemClock.elapsedRealtime()
        val ret: Int
        if (!hasPermission) {
            setFingerPrintDeviceTextOnUIThread("Permission denied")
            return
        }
        try {
            if (vid == 1204 || vid == 11279) {
                if (pid == 34323) {
                    ret = mfs100.LoadFirmware()
                    if (ret != 0) {
                        setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
                    } else {
                        setFingerPrintDeviceTextOnUIThread("Load firmware success")
                    }
                } else if (pid == 4101) {
                    val key = "Without Key"
                    ret = mfs100.Init()
                    if (ret == 0) {
                        showSuccessLog(key)
                    } else {
                        setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private var mLastDttTime = 0L

    override fun OnDeviceDetached() {
        try {

            if (SystemClock.elapsedRealtime() - mLastDttTime < Threshold) {
                return
            }
            mLastDttTime = SystemClock.elapsedRealtime()
            unInitScanner()

            setFingerPrintDeviceTextOnUIThread("Device removed")
        } catch (e: Exception) {
        }

    }

    override fun OnHostCheckFailed(err: String) {
        try {
            Toast.makeText(applicationContext, err, Toast.LENGTH_LONG).show()
        } catch (ignored: Exception) {
        }

    }

    private fun setFingerPrintDeviceTextOnUIThread(str: String) {

        tvDeviceMsg.post {
            try {
                tvDeviceMsg.text = str
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun unInitScanner() {
        try {
            val ret = mfs100.UnInit()
            if (ret != 0) {
                setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
            } else {
                showLonOnLogcat("Device is not ready")
                setFingerPrintDeviceTextOnUIThread("Device is not ready")
                leftCapFingerData = null
                rightCapFingerData = null
            }
        } catch (e: Exception) {
            Log.e("unInitScanner.EX", e.toString())
        }
    }

    override fun onStart() {
        try {
            if (mfs100 == null) {
                initMFS100()
            } else {
                initScanner()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onStart()
    }

    override fun onStop() {
        try {
            if (isCaptureRunning) {
                val ret = mfs100.StopAutoCapture()
            }
            Thread.sleep(500)
            //            unInitScanner();
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        super.onStop()
    }

    override fun onDestroy() {
        try {
            if (mfs100 != null) {
                mfs100.Dispose()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun initScanner() {
        try {
            val ret = mfs100.Init()
            if (ret != 0) {
                setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
            } else {
                setFingerPrintDeviceTextOnUIThread("Device is ready")
                /*val info = ("Serial: " + mfs100.GetDeviceInfo().SerialNo()
                        + " Make: " + mfs100.GetDeviceInfo().Make()
                        + " Model: " + mfs100.GetDeviceInfo().Model()
                        + "\nCertificate: " + mfs100.GetCertification())
                showLonOnLogcat(info)*/
            }
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, "Init failed, unhandled exception",
                Toast.LENGTH_LONG
            ).show()
            setFingerPrintDeviceTextOnUIThread("Init failed, unhandled exception")
        }

    }

    private var isCaptureRunning = false

    private fun startSyncLeftFingerCapture() {
        Thread(Runnable {
            setFingerPrintDeviceTextOnUIThread("")
            isCaptureRunning = true
            try {
                val fingerData = FingerData()
                val ret = mfs100.AutoCapture(fingerData, timeout, true)
                Log.e("StartSyncCapture.RET", "" + ret)
                if (ret != 0) {
                    setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
                } else {
                    leftCapFingerData = fingerData

                    val bitmap = BitmapFactory.decodeByteArray(
                        fingerData.FingerImage(), 0,
                        fingerData.FingerImage().size
                    )
                    this@UserEditActivity.runOnUiThread { ivLeftFinger.setImageBitmap(bitmap) }

                    setFingerPrintDeviceTextOnUIThread("Capture Success")
                    tvLeftFingerCaptureMsg.text = "Captured"
                    fingerLog(fingerData)
                    setLeftFingerData(fingerData)

                }
            } catch (ex: Exception) {
                setFingerPrintDeviceTextOnUIThread("Error")
            } finally {
                isCaptureRunning = false
            }
        }).start()
    }

    private fun startSyncRightFingerCapture() {
        Thread(Runnable {
            setFingerPrintDeviceTextOnUIThread("")
            isCaptureRunning = true
            try {
                val fingerData = FingerData()
                val ret = mfs100.AutoCapture(fingerData, timeout, true)
                Log.e("StartSyncCapture.RET", "" + ret)
                if (ret != 0) {
                    setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
                } else {
                    rightCapFingerData = fingerData

                    val bitmap = BitmapFactory.decodeByteArray(
                        fingerData.FingerImage(), 0,
                        fingerData.FingerImage().size
                    )
                    this@UserEditActivity.runOnUiThread { ivRightFinger.setImageBitmap(bitmap) }

                    //Log.e("RawImage", Base64.encodeToString(fingerData.RawData(), Base64.DEFAULT));
                    //                        Log.e("FingerISOTemplate", Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT));

                    setFingerPrintDeviceTextOnUIThread("Capture Success")
                    tvRightFingerCaptureMsg.text = "Captured"
                    fingerLog(fingerData)
                    setRightFingerData(fingerData)
                }
            } catch (ex: Exception) {
                setFingerPrintDeviceTextOnUIThread("Error")
            } finally {
                isCaptureRunning = false
            }
        }).start()
    }

    private fun fingerLog(fingerData: FingerData) {
        val log = ("\nQuality: " + fingerData.Quality()
                + "\nNFIQ: " + fingerData.Nfiq()
                + "\nWSQ Compress Ratio: "
                + fingerData.WSQCompressRatio()
                + "\nImage Dimensions (inch): "
                + fingerData.InWidth() + "\" X "
                + fingerData.InHeight() + "\""
                + "\nImage Area (inch): " + fingerData.InArea()
                + "\"" + "\nResolution (dpi/ppi): "
                + fingerData.Resolution() + "\nGray Scale: "
                + fingerData.GrayScale() + "\nBits Per Pixal: "
                + fingerData.Bpp() + "\nWSQ Info: "
                + fingerData.WSQInfo())
        showLonOnLogcat(log)
    }

    private fun setLeftFingerData(
        fingerData: FingerData
    ) {
        try {
            if (scannerAction == ScannerAction.Capture) {
                leftEnrollTemplate = ByteArray(fingerData.ISOTemplate().size)

                System.arraycopy(
                    fingerData.ISOTemplate(), 0, leftEnrollTemplate, 0,
                    fingerData.ISOTemplate().size
                )
            } else if (scannerAction == ScannerAction.Verify) {
                if (leftEnrollTemplate == null) {
                    return
                }
                leftVerifyTemplate = ByteArray(fingerData.ISOTemplate().size)
                System.arraycopy(
                    fingerData.ISOTemplate(), 0, leftVerifyTemplate, 0,
                    fingerData.ISOTemplate().size
                )
                val ret = mfs100.MatchISO(leftEnrollTemplate, leftVerifyTemplate)
                if (ret < 0) {
                    setFingerPrintDeviceTextOnUIThread(
                        "Error: $ret(" + mfs100.GetErrorMsg(
                            ret
                        ) + ")"
                    )
                } else {
                    if (ret >= 96) {
                        user.leftFingerISOTemplateDataByteArray = fingerData.ISOTemplate()
                        user.leftFingerDataBase64 =
                            Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)
                        user.leftFingerISOTemplateDataBase64 =
                            Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT)
                        tvLeftFingerVerifyMsg.text = "Finger matched"
                    } else {
                        tvLeftFingerVerifyMsg.text = "Finger not matched"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun setRightFingerData(
        fingerData: FingerData
    ) {
        try {
            if (scannerAction == ScannerAction.Capture) {
                rightEnrollTemplate = ByteArray(fingerData.ISOTemplate().size)
                System.arraycopy(
                    fingerData.ISOTemplate(), 0, rightEnrollTemplate, 0,
                    fingerData.ISOTemplate().size
                )
            } else if (scannerAction == ScannerAction.Verify) {
                if (rightEnrollTemplate == null) {
                    return
                }
                rightVerifyTemplate = ByteArray(fingerData.ISOTemplate().size)
                System.arraycopy(
                    fingerData.ISOTemplate(), 0, rightVerifyTemplate, 0,
                    fingerData.ISOTemplate().size
                )
                val ret = mfs100.MatchISO(rightEnrollTemplate, rightVerifyTemplate)
                if (ret < 0) {
                    setFingerPrintDeviceTextOnUIThread(
                        "Error: $ret(" + mfs100.GetErrorMsg(
                            ret
                        ) + ")"
                    )
                } else {
                    if (ret >= 96) {
                        user.rightFingerISOTemplateDataByteArray = fingerData.ISOTemplate()
                        user.rightFingerDataBase64 =
                            Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)
                        user.rightFingerISOTemplateDataBase64 =
                            Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT)
                        tvRightFingerVerifyMsg.text = "Finger matched"
                    } else {
                        tvRightFingerVerifyMsg.text = "Finger not matched"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}
