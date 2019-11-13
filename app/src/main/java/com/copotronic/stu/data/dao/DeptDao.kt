package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Department

@Dao

interface DeptDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dept: Department)

    @Query("select * from department")
    fun all() : LiveData<List<Department>>
}