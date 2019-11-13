package com.copotronic.stu.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.copotronic.stu.R
import com.copotronic.stu.model.User

class UserRvAdapter(private val context: Context, private val users: ArrayList<User>) :
    RecyclerViewAdapterThd<User>(users) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyUserVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_user_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val h = holder as MyUserVH
        h.bind(getItem(h.adapterPosition))
    }

    private inner class MyUserVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvUserId = v.findViewById<TextView>(R.id.tvUserId)
        private val tvName = v.findViewById<TextView>(R.id.tvName)

        fun bind(u: User) {
            tvName.text = u.name
            tvUserId.text = u.userId
        }

    }
}