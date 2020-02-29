package com.copotronic.stu.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.copotronic.stu.R
import com.copotronic.stu.activities.user.UserDetailsActivity
import com.copotronic.stu.activities.user.UserEditActivity
import com.copotronic.stu.data.AppDb
import com.copotronic.stu.model.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    private inner class MyUserVH(val v: View) : RecyclerView.ViewHolder(v) {
        private val tvUserId = v.findViewById<TextView>(R.id.tvUserId)
        private val tvName = v.findViewById<TextView>(R.id.tvName)
        private val btnDelete = v.findViewById<Button>(R.id.btnDelete)
        private val btnEdit = v.findViewById<Button>(R.id.btnEdit)
        private val ivUser = v.findViewById<ImageView>(R.id.ivUser)

        @SuppressLint("CheckResult")
        fun bind(u: User) {
            tvName.text = u.name
            tvUserId.text = u.id.toString()


            try {
//                val userLeftFingerByteData = Base64.decode(u.leftFingerDataBase64, Base64.DEFAULT)
//                val leftFingerBitmap = BitmapFactory.decodeByteArray(
//                    userLeftFingerByteData,
//                    0,
//                    userLeftFingerByteData.size
//                )
//                ivUser.setImageBitmap(leftFingerBitmap)

                Observable.fromCallable {
                    Log.d("DATATAG", u.imagePath)

                    val myBitmap = BitmapFactory.decodeFile(u.imagePath)
                    myBitmap
                }.observeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({myBitmap->
                        ivUser.setImageBitmap(myBitmap)}, {it.printStackTrace()})
            } catch (e: Exception) {
                e.printStackTrace()
            }

            btnEdit.setOnClickListener {
                val intent = Intent(context, UserEditActivity::class.java)
                intent.putExtra("user", u)
                context.startActivity(intent)
            }



            btnDelete.setOnClickListener {
                db.userDao().delete(u)
            }

            v.setOnClickListener {
                val intent = Intent(context, UserDetailsActivity::class.java)
                intent.putExtra("user", u)
                context.startActivity(intent)
            }
        }

    }
}