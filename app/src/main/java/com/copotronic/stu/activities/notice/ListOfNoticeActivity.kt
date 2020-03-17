package com.copotronic.stu.activities.notice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.copotronic.stu.R
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

        val adapter = NoticeListRvAdapter(this, ArrayList())
        rvNotices.adapter = adapter

        dao?.all()?.observe(this, Observer {
            adapter.addUniquely(it as java.util.ArrayList<Notice>)
        })

        floatAddNotice.setOnClickListener {
            startActivity(Intent(this, AddNoticeActivity::class.java))
        }

        evNoticeSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                s.let {
                    adapter.filter.filter(s)
                }
            }
        })
    }
}
