package com.copotronic.stu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Notice

@Dao
interface NoticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notice: Notice)

    @Query("select * from notice where notice_date = :date")
    fun noticeByDate(date: String): Notice
}