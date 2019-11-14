package com.copotronic.stu.adapters.spin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.copotronic.stu.R
import com.copotronic.stu.model.Designation

class CustomDesignationSpinAdapter(context: Context, private val resource: Int,
                                   val items: ArrayList<Designation>)
    : ArrayAdapter<Designation>(context, resource, items) {


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView

        if (convertView == null)
            v = LayoutInflater.from(context).inflate(resource, parent, false)

        val item = items[position]
        val tvName = v!!.findViewById<TextView>(R.id.tvName)
        tvName.text = item.name

        val rightDropDownView = v.findViewById<ImageView>(R.id.ivDown)
        rightDropDownView.visibility = View.GONE

        return v
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView

        if (convertView == null)
            v = LayoutInflater.from(context).inflate(resource, parent, false)

        val item = items[position]
        val tvName = v!!.findViewById<TextView>(R.id.tvName)
        tvName.text = item.name

        return v
    }

}