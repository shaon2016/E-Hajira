package com.copotronic.stu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    var userId: String = "",
    var name: String = "",
    var mobile: String = "",
    @ColumnInfo(name = "user_type_id")
    var userTypeId: Int = 0,
    @ColumnInfo(name = "user_designation_id")
    var designationId: Int = 0,
    @ColumnInfo(name = "user_department_id")
    var departmentId: Int = 0,
    @ColumnInfo(name = "user_section_id")
    var sectionId: Int = 0,
    @ColumnInfo(name = "user_shift_id")
    var shiftId: Int = 0,
    @ColumnInfo(name = "line_desc_id")
    var lineDescriptionId: Int = 0,

    @ColumnInfo(name = "user_pin")
    var pinNo : String = "",
    @ColumnInfo(name = "image_path")
    var imagePath : String = "",
    @ColumnInfo(name = "left_finger_base64")
    var leftFingerDataBase64 : String = "",
    @ColumnInfo(name = "right_finger_base64")
    var rightFingerDataBase64 : String = "",
    @ColumnInfo(name = "left_finger_iso_template_base64")
    var leftFingerISOTemplateDataBase64 : String = "",
    @ColumnInfo(name = "right_finger_iso_template_base64")
    var rightFingerISOTemplateDataBase64 : String = "",
    @ColumnInfo(name = "left_finger_iso_template_byte_array", typeAffinity = ColumnInfo.BLOB)
    var leftFingerISOTemplateDataByteArray : ByteArray = byteArrayOf(),
    @ColumnInfo(name = "right_finger_iso_template_byte_array", typeAffinity = ColumnInfo.BLOB)
    var rightFingerISOTemplateDataByteArray : ByteArray = byteArrayOf()


) : Serializable