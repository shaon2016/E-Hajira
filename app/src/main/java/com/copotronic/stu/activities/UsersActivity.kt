package com.copotronic.stu.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.copotronic.stu.R
import com.copotronic.stu.adapters.UserRvAdapter
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.User
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {

    private lateinit var db: AppDb
    private lateinit var adapter : UserRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        db = AppDb.getInstance(this)!!
        initView()
    }

    private fun initView() {
        rvUsers.layoutManager = LinearLayoutManager(this)

        setUsers()

    }

    private fun setUsers() {
        val user = User(0, "1231", "Shaon")
        db.userDao().insert(user)
        db.userDao().all().observe(this, Observer { users ->
            adapter = UserRvAdapter(this, users as ArrayList<User>)
            rvUsers.adapter = adapter
        })
    }
}
