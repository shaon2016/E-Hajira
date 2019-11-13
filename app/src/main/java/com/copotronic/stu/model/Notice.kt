package com.copotronic.stu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notice(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "image_file_path")
    var imageFilePath: String,
    @ColumnInfo(name = "user_type_id")
    var userTypeId : Int,
    @ColumnInfo(name = "notice_text")
    var noticeText: String
)