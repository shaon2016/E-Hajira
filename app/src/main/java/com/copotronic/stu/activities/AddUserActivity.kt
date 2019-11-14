package com.copotronic.stu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.copotronic.stu.R
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.*
import kotlinx.android.synthetic.main.activity_add_user.*

class AddUserActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private var desgId = 0
    private var secId = 0
    private var shiftId = 0
    private var deptId = 0
    private var typeId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        initVar()
        initView()
    }

    private fun initVar() {
        db = AppDb.getInstance(this)!!
    }

    private fun initView() {
        setDesignation()
        setDepts()
        setSection()
        setShifts()
        setUserTypes()
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
