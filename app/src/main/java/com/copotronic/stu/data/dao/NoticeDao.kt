package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.copotronic.stu.model.Notice

@Dao
interface NoticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notice: Notice)

    @Query("select * from notice where notice_date = :date and user_type_id = 0")
    fun noticeByDate(date: String): Notice

    @Query("select * from notice where user_type_id = :typeId and notice_date = :date")
    fun noticeByUserTypeId(typeId: Int, date: String): Notice

    @Query("select * from notice where notice_type = :typeId and notice_date = :date")
    fun noticeByNoticeTypeId(typeId: Int, date: String): Notice


    @Query("select * from notice")
    fun all() : LiveData<List<Notice>>

    @Delete
    fun delete(notice: Notice)

    @Query("select * from notice where id = :id")
    fun notice(id: Int) : Notice
}