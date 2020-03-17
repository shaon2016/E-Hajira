package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.LineDescription

@Dao
interface LineDescriptionDao {

    @Query("select * from linedescription")
    fun all() : LiveData<List<LineDescription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(des: LineDescription)

}