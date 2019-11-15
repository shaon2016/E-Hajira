package com.copotronic.stu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.UserType
import kotlinx.android.synthetic.main.activity_add_notice.*

class AddNoticeActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private var typeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notice)

        initVar()
        initView()
    }

    private fun initVar() {

    }

    private fun initView() {
        setUserTypes()
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

}
