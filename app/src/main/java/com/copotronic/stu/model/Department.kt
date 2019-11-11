package com.copotronic.stu.model

import androidx.room.PrimaryKey

data class Department(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String

)