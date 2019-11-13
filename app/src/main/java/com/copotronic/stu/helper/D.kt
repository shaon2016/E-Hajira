package com.copotronic.stu.helper

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

object D {

    fun showToastShort(ctx: Context, message: String) {
        val toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT)
        val tv = toast.view.findViewById<TextView>(android.R.id.message)
        if (tv != null)
            tv.gravity = Gravity.CENTER
        toast.show()
    }

    fun showToastLong(ctx: Context, message: String) {
        val toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG)
        val tv = toast.view.findViewById<TextView>(android.R.id.message)
        if (tv != null)
            tv.gravity = Gravity.CENTER
        toast.show()
    }
}