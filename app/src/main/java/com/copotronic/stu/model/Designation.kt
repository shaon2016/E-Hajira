package com.copotronic.stu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Designation( @PrimaryKey(autoGenerate = true)
                        val id: Int,
                        var name: String)