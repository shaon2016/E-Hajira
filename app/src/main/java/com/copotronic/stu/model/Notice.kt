package com.copotronic.stu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Notice(
    @ColumnInfo(name = "image_file_path")
    var imageFilePath: String = "",
    @ColumnInfo(name = "user_type_id")
    var userTypeId: Int = 0,
    @ColumnInfo(name = "notice_text")
    var noticeText: String = "",
    @ColumnInfo(name = "notice_date")
    var noticeDate: String = "",
    @ColumnInfo(name = "notice_type")
    var noticeType : Int = 0, // 0 = user type, 1 = Student Type
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
    ) : Serializable