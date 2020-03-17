package com.copotronic.stu.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.copotronic.stu.R
import com.copotronic.stu.activities.notice.AddNoticeActivity
import com.copotronic.stu.activities.notice.NoticeEditActivity
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.Notice
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class NoticeListRvAdapter(val context: Context, val notices: ArrayList<Notice>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    /** Hold filterable list*/
    private var itemListFiltered: ArrayList<Notice> = notices


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyNoticeVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_notice_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = itemListFiltered.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val h = holder as MyNoticeVH
        val curPos = h.adapterPosition
        val notice = itemListFiltered[curPos]

        h.bind(notice)
    }

    private inner class MyNoticeVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvNotice = v.findViewById<TextView>(R.id.tvNotice)
        private val tvDate = v.findViewById<TextView>(R.id.tvDate)
        private val tvNoticeType = v.findViewById<TextView>(R.id.tvNoticeType)
        private val btnDelete = v.findViewById<Button>(R.id.btnDelete)
        private val btnEdit = v.findViewById<Button>(R.id.btnEdit)

        fun bind(notice: Notice) {
            if (notice.noticeDate.isNotEmpty())
                tvDate.text = notice.noticeDate
            if (notice.noticeText.isNotEmpty())
                tvNotice.text = notice.noticeText

            btnDelete.setOnClickListener {
                Observable.fromCallable {
                        AppDb.getInstance(context)?.noticeDao()?.delete(notice)
                    }.subscribeOn(Schedulers.io())
                    .subscribe({}, { it.printStackTrace() })
            }
            btnEdit.setOnClickListener {
                context.startActivity(Intent(context, NoticeEditActivity::class.java).apply {
                    putExtra("notice", notice)
                })
            }

            tvNoticeType.text = when (notice.noticeType) {
                0 -> "Home"
                1 -> "Other"
                else -> ""
            }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                itemListFiltered = if (charString.isEmpty()) {
                    notices
                } else {
                    val filteredList = ArrayList<Notice>()
                    for (row in notices) {
                        if (row.noticeText.toLowerCase().startsWith(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = itemListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: Filter.FilterResults
            ) {
                itemListFiltered = filterResults.values as ArrayList<Notice>
                notifyDataSetChanged()
            }
        }
    }

    fun addUniquely(newList: java.util.ArrayList<Notice>) {
        for (ad in newList) {
            val id = ad.id
            val exists = notices.any { it.id == id }
            if (!exists)
                add(ad)
        }
    }

    fun add(a: Notice) {
        notices.add(a)
        notifyItemInserted(itemCount - 1)
    }


}