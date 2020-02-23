package com.copotronic.stu.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.copotronic.stu.R
import com.copotronic.stu.model.Notice

class NoticeListRvAdapter(val context: Context, val notices: ArrayList<Notice>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyNoticeVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_notice_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = notices.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val h = holder as MyNoticeVH
        val curPos = h.adapterPosition
        val notice = notices[curPos]

        h.bind(notice)
    }

    private inner class MyNoticeVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvNotice = v.findViewById<TextView>(R.id.tvNotice)
        private val tvDate = v.findViewById<TextView>(R.id.tvDate)
        private val ivNotice = v.findViewById<ImageView>(R.id.ivNotice)
        private val btnDelete = v.findViewById<Button>(R.id.btnDelete)
        private val btnEdit = v.findViewById<Button>(R.id.btnEdit)

        fun bind(notice: Notice) {
            if (notice.noticeDate.isNotEmpty())
                tvDate.text = notice.noticeDate
            if (notice.noticeText.isNotEmpty())
                tvNotice.text = notice.noticeText

            if (notice.imageFilePath.isNotEmpty())
                Glide.with(context)
                    .load(notice.imageFilePath)
                    .into(ivNotice)
        }

    }
}