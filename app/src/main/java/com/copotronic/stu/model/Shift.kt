package com.copotronic.stu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shift( @PrimaryKey(autoGenerate = true)
                  val id: Int,
                  var name: String){

    override fun toString() = name
}