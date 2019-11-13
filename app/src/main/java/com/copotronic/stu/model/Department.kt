package com.copotronic.stu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Department(
    var name: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

)