package com.copotronic.stu.activities

import android.Manifest
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mantra.mfs100.FingerData
import com.mantra.mfs100.MFS100
import com.mantra.mfs100.MFS100Event
import kotlinx.android.synthetic.main.activity_add_user.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddUserActivity : AppCompatActivity(), MFS100Event {


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

    private var leftFingerFingerImageDataInStr: String? = null
    private var rightFingerFingerImageDataInStr: String? = null
    private var leftFingerFingerImageDataInByteArray: ByteArray? = null
    private var rightFingerFingerImageDataInByteArray: ByteArray? = null
    private var leftFingerISOTemplateDataInStr: String? = null
    private var rightFingerISOTemplateDataInStr: String? = null

    // User image
    /** Request code for gallery image selection for ad post*/
    private val REQUEST_GALLERY_IMAGE = 231
    /**
     * User image pick from this path
    * */
    private var userImage: Image? = null
    /**
     * User image saved into this path
     * */
    private var userImageFilePathTo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        initVar()
        initView()
    }

    private fun initVar() {
        db = AppDb.getInstance(this)!!

        initMFS100()
    }

    private fun initMFS100() {
        try {
            mfs100 = MFS100(this)
            mfs100.SetApplicationContext(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initView() {
        setDesignation()
        setDepts()
        setSection()
        setShifts()
        setUserTypes()

        btnSubmit.setOnClickListener {
            save()
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
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            ImagePicker.create(this@AddUserActivity)
                                .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
                                .toolbarImageTitle(getString(R.string.tap_to_select)) // image selection title
                                .toolbarArrowColor(Color.BLACK)
                                .limit(1)
                                .showCamera(true)
                                .toolbarArrowColor(ContextCompat.getColor(this@AddUserActivity, R.color.white))
                                .start(REQUEST_GALLERY_IMAGE)   }else
                            D.showToastLong(this@AddUserActivity, "Plz Grant the permissions")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()




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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            val image = ImagePicker.getFirstImageOrNull(data)

            this.userImage = image

            showUserImage()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showUserImage() {
        val myBitmap = BitmapFactory.decodeFile(userImage?.path)
        ivUser.setImageBitmap(myBitmap)
    }

    private fun copyFileToDestination() {

        val calender = Calendar.getInstance()

        val src = File(userImage!!.path)
        val destination = File(

            getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), "${calender.timeInMillis}${userImage?.name}"
        )
        userImageFilePathTo = destination.absolutePath
        Log.d("DATATAG", userImageFilePathTo)
        Thread {
            U.copyOrMoveFile(src, destination, true)
        }.start()
    }

    private fun save() {
        val name = evName.text.toString()
        val userId = evUserId.text.toString()
        val mobileNo = evMobile.text.toString()
        val pin = evPin.text.toString()
        val desc = evLineDescription.text.toString()


        if (pin.isNullOrEmpty()) {
            D.showToastShort(this, "Insert user pin")
            return
        }
        if (userId.isNullOrEmpty()) {
            D.showToastShort(this, "Insert user id")
            return
        }
        if (desc.isNullOrEmpty()) {
            D.showToastShort(this, "Insert user line description")
            return
        }
        if (name.isNullOrEmpty()) {
            D.showToastShort(this, "Insert name")
            return
        }
        if (mobileNo.isNullOrEmpty()) {
            D.showToastShort(this, "Insert mobile no")
            return
        }

//        if (leftFingerFingerImageDataInStr.isNullOrEmpty()) {
//            D.showToastShort(this, "Left finger data is missing")
//            return
//        }
//        if (rightFingerFingerImageDataInStr.isNullOrEmpty()) {
//            D.showToastShort(this, "Right finger data is missing")
//            return
//        }

        userImage?.let {
            copyFileToDestination()
        }

        Thread {
            val user = User(
                0, userId, name,mobileNo, typeId, desgId, deptId, secId, shiftId, pin,
                userImageFilePathTo, desc, leftFingerFingerImageDataInStr ?: "",
                rightFingerFingerImageDataInStr ?: "",
                leftFingerISOTemplateDataInStr ?: "",
                rightFingerISOTemplateDataInStr ?: "",

                leftFingerFingerImageDataInByteArray ?: byteArrayOf(),
                rightFingerFingerImageDataInByteArray ?: byteArrayOf()
            )
            db.userDao().insert(user)
        }.start()

        D.showToastShort(this, "User saved successfully")
        finish()
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
                            desgId = desgs[position].id
                        }
                    }
            } else spinUserDesignation.visibility = View.GONE
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

                spinSection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        secId = secs[position].id
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
                            deptId = depts[position].id
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

                spinUserShift.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        shiftId = shifts[position].id
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
                    this@AddUserActivity.runOnUiThread { ivLeftFinger.setImageBitmap(bitmap) }

/*                    Log.e("RawImage", Base64.encodeToString(fingerData.RawData(), Base64.DEFAULT));
                    Log.e("FingerISOTemplate", Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT));

                    val a =  Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)
                    val b = Base64.decode(a, Base64.DEFAULT)

                    if (fingerData.FingerImage()!=null && fingerData.FingerImage()!!.contentEquals(b)) {
                        Log.d("DATATAG", "match")
                    }
                    val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                    this@AddUserActivity.runOnUiThread { ivLeftFinger.setImageBitmap(bitmap) }*/

                    setFingerPrintDeviceTextOnUIThread("Capture Success")
                    tvLeftFingerCaptureMsg.text = "Captured"
                    fingerLog(fingerData)
                    setLeftFingerData(fingerData)

                    Log.d("DATATAG", Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT))
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
                    this@AddUserActivity.runOnUiThread { ivRightFinger.setImageBitmap(bitmap) }

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
        fingerData: FingerData) {
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
                        leftFingerFingerImageDataInByteArray = fingerData.ISOTemplate()
                        leftFingerFingerImageDataInStr = Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)
                        leftFingerISOTemplateDataInStr = Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT)
                        tvLeftFingerVerifyMsg.text = "Finger matched"
                    } else {
                        tvLeftFingerVerifyMsg.text = "Finger not matched"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*try {
            WriteFile("Raw.raw", fingerData.RawData())
            WriteFile("Bitmap.bmp", fingerData.FingerImage())
            WriteFile("ISOTemplate.iso", fingerData.ISOTemplate())
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

    }
    private fun setRightFingerData(
        fingerData: FingerData) {
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
                        rightFingerFingerImageDataInByteArray = fingerData.ISOTemplate()
                        rightFingerFingerImageDataInStr = Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)
                        rightFingerISOTemplateDataInStr = Base64.encodeToString(fingerData.ISOTemplate(), Base64.DEFAULT)
                        tvRightFingerVerifyMsg.text = "Finger matched"
                    } else {
                        tvRightFingerVerifyMsg.text = "Finger not matched"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*try {
            WriteFile("Raw.raw", fingerData.RawData())
            WriteFile("Bitmap.bmp", fingerData.FingerImage())
            WriteFile("ISOTemplate.iso", fingerData.ISOTemplate())
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

    }

}
