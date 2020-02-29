package com.copotronic.stu.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.copotronic.stu.R
import com.copotronic.stu.activities.notice.AddNoticeActivity
import com.copotronic.stu.adapters.NoticeListRvAdapter
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.Notice
import kotlinx.android.synthetic.main.activity_list_of_notice.*

class ListOfNoticeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notice)

        val dao = AppDb.getInstance(this)?.noticeDao()


        rvNotices.layoutManager = LinearLayoutManager(this)
        dao?.all()?.observe(this, Observer {
            rvNotices.adapter = NoticeListRvAdapter(this, it as ArrayList<Notice>)
        })

        floatAddNotice.setOnClickListener {
            startActivity(Intent(this, AddNoticeActivity::class.java))
        }
    }
}
