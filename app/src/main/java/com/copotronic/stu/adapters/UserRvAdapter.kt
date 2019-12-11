package com.copotronic.stu.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.copotronic.stu.R
import com.copotronic.stu.activities.AddUserActivity
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.User
import java.lang.Exception

class UserRvAdapter(
    private val context: Context,
    private val users: ArrayList<User>,
    val db: AppDb
) :
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
        private val btnDelete = v.findViewById<Button>(R.id.btnDelete)
        private val btnEdit = v.findViewById<Button>(R.id.btnEdit)
        private val ivUser = v.findViewById<ImageView>(R.id.ivUser)

        fun bind(u: User) {
            tvName.text = u.name
            tvUserId.text = u.userId
//            val myBitmap = BitmapFactory.decodeFile(u.imagePath)
//            ivUser.setImageBitmap(myBitmap)

            try {
                val userLeftFingerByteData = Base64.decode(u.leftFingerDataBase64, Base64.DEFAULT)
                val leftFingerBitmap = BitmapFactory.decodeByteArray(
                    userLeftFingerByteData,
                    0,
                    userLeftFingerByteData.size
                )
                ivUser.setImageBitmap(leftFingerBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            btnEdit.setOnClickListener {
                val intent = Intent(context, AddUserActivity::class.java)
                intent.putExtra("user", u)
                intent.putExtra("user_edit", true)
                context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                db.userDao().delete(u)
            }
        }

    }
}