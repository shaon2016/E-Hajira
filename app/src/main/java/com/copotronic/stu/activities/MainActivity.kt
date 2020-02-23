package com.copotronic.stu.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.copotronic.stu.R
import com.copotronic.stu.ScannerAction
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.helper.D
import com.copotronic.stu.helper.U
import com.copotronic.stu.model.User
import com.mantra.mfs100.FingerData
import com.mantra.mfs100.MFS100
import com.mantra.mfs100.MFS100Event
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.ivNotice
import kotlinx.android.synthetic.main.activity_main.tvDate
import kotlinx.android.synthetic.main.activity_main.tvNotice
import kotlinx.android.synthetic.main.activity_main.tvTime
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.android.synthetic.main.toolbar.*
import java.time.LocalDate
import java.util.*

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
        supportActionBar?.title = ""


        db = AppDb.getInstance(this)!!

        initMFS100()

        handleBtn()

        setNotice()

        setDateTime()

        handleGifImages()
    }

    private fun handleGifImages() {

        Glide.with(this)
            .asGif()
            .load(R.drawable.finger_on)
            .into(ivFingerOn)

        Glide.with(this)
            .asGif()
            .load(R.drawable.touch_korun)
            .into(ivTouchKorun)
    }

    private fun setDateTime() {
        tvDate.text = "Date: ${U.todayDate}"
        tvTime.text = "Time: ${U.nowTime}"
    }

    /**
     * Showing notice according to date
     * */
    @SuppressLint("CheckResult")
    private fun setNotice() {
        showCountDownMujibBorso()

        Observable.fromCallable {
            val todayDate = U.reformatDate(Calendar.getInstance().time, "yyyy-MM-dd")
            db.noticeDao().noticeByDate(todayDate)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ notice ->
                if (notice != null) {
                    tvNotice.text = notice.noticeText
                    if (notice.imageFilePath.isNotEmpty())
                        Observable.fromCallable {
                            val myBitmap = BitmapFactory.decodeFile(notice.imageFilePath)
                            myBitmap
                        }.subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ myBitmap ->
                                ivNotice.setImageBitmap(myBitmap)
                            }, {
                                tvNotice.text = ""
                                ivNotice.visibility = View.GONE

                                it.printStackTrace()
                            })
                    else ivNotice.visibility = View.GONE
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

    private fun showCountDownMujibBorso() {
        val futureMinDate = U.parseDateSimple("2020-03-17")
        tvMujibCountDownTime.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.green))

        // Here futureMinDate.time Returns the number of milliseconds since January 1, 1970, 00:00:00 GM
        // So we need to subtract the millis from current millis to get actual millis
        object : CountDownTimer(futureMinDate.time - System.currentTimeMillis(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 1000) % 60
                val min = (millisUntilFinished / (1000 * 60)) % 60
                val hr = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val day = ((millisUntilFinished / (1000 * 60 * 60)) / 24).toInt()
                val formattedTimeStr = if (day > 1) "$day Days $hr : $min : $sec"
                else "$day day $hr : $min : $sec"
                tvMujibCountDownTime.text = formattedTimeStr
            }

            override fun onFinish() {
                tvMujibCountDownTime.text = "Done!"
                tvMujibCountDownTime.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red_soothing))
            }
        }.start()
    }

    private fun handleBtn() {
        ivFingerOn.setOnClickListener {

            scannerAction = ScannerAction.Capture
            startSyncFingerCapture()

            ivFingerDin.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(R.drawable.finger_din)
                .into(ivFingerDin)

            ivTouchKorun.visibility = View.GONE

        }
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
                    val allUser = db.userDao().allUser()

                    setFingerPrintDeviceTextOnUIThread("Capture Success")

                    // Checking all user to match our current user finger print
                    var isMatched = false
                    var user = User()
                    for (i in allUser.indices) {
                        val u = allUser[i]
                        isMatched = matchFinger(u, fingerData, ret)

                        if (isMatched) {
                            user = u
                            break
                        }
                    }

                    this@MainActivity.runOnUiThread {
                        if (isMatched) {
                            D.showToastLong(this, "Finger matched")
                            startActivity(Intent(
                                this,
                                UserDetailsActivity::class.java
                            ).apply {
                                putExtra("user", user)
                            })
                        } else {
                            D.showToastLong(this, "Finger not matched")

                        }
                    }
                    //  fingerLog(fingerData)
                }
            } catch (ex: Exception) {
                setFingerPrintDeviceTextOnUIThread("Error")
                ex.printStackTrace()
            } finally {
                isCaptureRunning = false
            }
        }).start()
    }

    private fun matchFinger(user: User, fingerData: FingerData, ret: Int): Boolean {
        val fingerTemplate = ByteArray(fingerData.ISOTemplate().size)
        System.arraycopy(
            fingerData.ISOTemplate(), 0, fingerTemplate, 0,
            fingerData.ISOTemplate().size
        )

        val savedLeftFingerISOTemplateData =
            Base64.decode(user.leftFingerISOTemplateDataBase64, Base64.DEFAULT)
        val savedRightFingerISOTemplateData =
            Base64.decode(user.rightFingerISOTemplateDataBase64, Base64.DEFAULT)

        val leftFingerMatchRet =
            mfs100.MatchISO(fingerTemplate, savedLeftFingerISOTemplateData)
        val rightFingerMatchRet =
            mfs100.MatchISO(fingerTemplate, savedRightFingerISOTemplateData)

        return if (leftFingerMatchRet < 0 || rightFingerMatchRet < 0) {
            false
        } else {
            leftFingerMatchRet >= 96 || rightFingerMatchRet >= 96
        }
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
        when (item.itemId) {
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