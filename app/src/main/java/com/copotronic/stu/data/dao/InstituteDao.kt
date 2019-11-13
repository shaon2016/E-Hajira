package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Institute

@Dao
interface InstituteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inst: Institute)


    @Query("select * from institute")
    fun all() : LiveData<List<Institute>>
}