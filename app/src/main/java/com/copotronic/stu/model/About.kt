package com.copotronic.stu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class About(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "device_id")
    var deviceId: String = "",
    @ColumnInfo(name = "device_mac")
    var deviceMac: String = "",
    @ColumnInfo(name = "app_version")
    var appVersion: String = "",
    @ColumnInfo(name = "sim_number")
    var simNumber: Int = 0
)