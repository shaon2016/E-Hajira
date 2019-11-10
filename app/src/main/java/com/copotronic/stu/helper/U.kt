package com.copotronic.stu.helper

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
}