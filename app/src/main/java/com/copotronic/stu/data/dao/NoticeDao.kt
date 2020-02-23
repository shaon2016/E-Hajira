package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Notice

@Dao
interface NoticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notice: Notice)

    @Query("select * from notice where notice_date = :date and user_type_id = 0")
    fun noticeByDate(date: String): Notice

    @Query("select * from notice where user_type_id = :typeId")
    fun noticeByUserTypeId(typeId: Int): Notice

    @Query("select * from notice")
    fun all() : LiveData<List<Notice>>
}