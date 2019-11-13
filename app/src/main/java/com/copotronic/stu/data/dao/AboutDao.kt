package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.copotronic.stu.model.About

@Dao
interface AboutDao {


    @Query("select * from about")
    fun all() : LiveData<List<About>>
}