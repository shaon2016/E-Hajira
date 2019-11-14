package com.copotronic.stu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Department(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String

){

    override fun toString() = name
}