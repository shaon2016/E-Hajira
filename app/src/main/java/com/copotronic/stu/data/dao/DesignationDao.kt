package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Designation

@Dao
interface DesignationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(des: Designation)

    @Query("select * from designation")
    fun all() : LiveData<List<Designation>>


}