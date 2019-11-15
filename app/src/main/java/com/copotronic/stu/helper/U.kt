package com.copotronic.stu.helper

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object U {

    fun getTimeInCalendarFormat(input: String): Calendar? {
        //Date/time pattern of input date
        val df = SimpleDateFormat("HH:mm:ss", Locale.US)

        return try {
            //Conversion of input String to date
            val date = df.parse(input)

            val cal = Calendar.getInstance()
            cal.time = date

            cal
        } catch (pe: ParseException) {
            pe.printStackTrace()
            null
        }
    }

    val todayDate: String
        get() {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = Date()
            return formatter.format(date)
        }

    val nowTime: String
        get() {
            val df = SimpleDateFormat("HH:mm:ss", Locale.US)
            val date = Date()
            return df.format(date)
        }

    /**
     * For src -> source folder and file
     * destination -> destination path and new name
     * */
    @Throws(IOException::class)
     fun copyOrMoveFile(srcFile: File, destination: File, isCopy: Boolean) {
        var outChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            outChannel = FileOutputStream(destination).channel
            inputChannel = FileInputStream(srcFile).channel
            inputChannel.transferTo(0, inputChannel.size(), outChannel)
            inputChannel.close()
            if (!isCopy)
                srcFile.delete()
        } finally {
            inputChannel?.close()
            outChannel?.close()
        }
    }
}