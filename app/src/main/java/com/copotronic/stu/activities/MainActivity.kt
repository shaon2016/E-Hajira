package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.copotronic.stu.R
import com.copotronic.stu.ScannerAction
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.D
import com.copotronic.stu.helper.U
import com.mantra.mfs100.FingerData
import com.mantra.mfs100.MFS100
import com.mantra.mfs100.MFS100Event
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity(), MFS100Event {
    private lateinit var db: AppDb

    private val Threshold: Long = 1500
    private var mLastAttTime = 0L
    private var timeout = 10000
    private var leftCapFingerData: FingerData? = null
    private var rightCapFingerData: FingerData? = null
    private var scannerAction = ScannerAction.Capture

    private lateinit var mfs100: MFS100
    private var isCaptureRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        db = AppDb.getInstance(this)!!

        initMFS100()

        handleBtn()


    }

    private fun handleBtn() {
        btnScanFinger.setOnClickListener {
            val userId = evUserId.text.toString()

            if (userId.isNullOrEmpty()) {
                D.showToastShort(this@MainActivity, "Insert user id")
                return@setOnClickListener
            }

            scannerAction = ScannerAction.Capture
            startSyncFingerCapture()
        }

//        btnSubmit.setOnClickListener {
//            validateUser()
//        }
    }

    private fun validateUser() {


    }

    private fun startSyncFingerCapture() {
        Thread(Runnable {
            setFingerPrintDeviceTextOnUIThread("")
            isCaptureRunning = true
            try {
                val fingerData = FingerData()
                val ret = mfs100.AutoCapture(fingerData, timeout, true)

                if (ret != 0) {
                    setFingerPrintDeviceTextOnUIThread(mfs100.GetErrorMsg(ret))
                } else {
                    val fingerRawDataInStr =
                        Base64.encodeToString(fingerData.FingerImage(), Base64.DEFAULT)


                    // check in db for both finger
                    val user = db.userDao().findUserByUserId(evUserId.text.toString())

//                    this@MainActivity.runOnUiThread {
//                        if (user != null) {
//                            D.showToastShort(this, "User found")
//
////                           startActivity(
////                               Intent(
////                                   this@MainActivity,
////                                   StudentDetailsActivity::class.java
////                               ).apply {
////                                   putExtra("user", user)
////                               })
//                        } else {
//                            D.showToastShort(this, "No match in database. Try again!")
//                        }
//                    }


                    val fingerTemplate = ByteArray(fingerData.ISOTemplate().size)
                    System.arraycopy(
                        fingerData.ISOTemplate(), 0, fingerTemplate, 0,
                        fingerData.ISOTemplate().size
                    )

                    if (user != null) {
                        this@MainActivity.runOnUiThread {
                            D.showToastShort(this, "User found")
                        }
                        val savedLeftFingerISOTemplateData =
                            Base64.decode(user.leftFingerISOTemplateDataBase64, Base64.DEFAULT)
                        val savedRightFingerISOTemplateData =
                            Base64.decode(user.rightFingerISOTemplateDataBase64, Base64.DEFAULT)


                        val leftFingerMatchRet =
                            mfs100.MatchISO(fingerTemplate, savedLeftFingerISOTemplateData)
                        val rightFingerMatchRet =
                            mfs100.MatchISO(fingerTemplate, savedRightFingerISOTemplateData)


                        if (leftFingerMatchRet < 0 || rightFingerMatchRet < 0) {
                            setFingerPrintDeviceTextOnUIThread(
                                "Error: $ret(" + mfs100.GetErrorMsg(
                                    ret
                                ) + ")"
                            )
                        } else {
                            if (leftFingerMatchRet >= 96 || rightFingerMatchRet >= 96) {
                                this@MainActivity.runOnUiThread {
                                    D.showToastLong(this@MainActivity, "Finger matched")
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            StudentDetailsActivity::class.java
                                        ).apply {
                                            putExtra("user", user)
                                        })
                                }
                            } else {
                                this@MainActivity.runOnUiThread {
                                    D.showToastLong(this@MainActivity, "Finger not matched")
                                }
                            }
                        }
                    } else {
                        this@MainActivity.runOnUiThread {
                            D.showToastShort(this, "No match in database. Try again!")
                        }
                    }


                    setFingerPrintDeviceTextOnUIThread("Capture Success")
                    fingerLog(fingerData)
                }
            } catch (ex: Exception) {
                setFingerPrintDeviceTextOnUIThread("Error")
                ex.printStackTrace()
            } finally {
                isCaptureRunning = false
            }
        }).start()
    }


    private fun initMFS100() {
        try {
            mfs100 = MFS100(this)
            mfs100.SetApplicationContext(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setFingerPrintDeviceTextOnUIThread(str: String) {
        tvFingerScannerMsg.post {
            try {
                tvFingerScannerMsg.text = str
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
            }
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, "Init failed, unhandled exception",
                Toast.LENGTH_LONG
            ).show()
            setFingerPrintDeviceTextOnUIThread("Init failed, unhandled exception")
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

}
