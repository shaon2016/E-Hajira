package com.copotronic.stu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Institute(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "institute_id")
    var instituteId: Int,
    var name: String,
    var address: String,
    @ColumnInfo(name = "loc_lat")
    var locLat: Double,
    @ColumnInfo(name = "loc_lon")
    var locLon: Double,
    @ColumnInfo(name = "created_at")
    var created_at: String,
    @ColumnInfo(name = "updated_at")
    var updated_at: String
)