package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.Section

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ut: Section)


    @Query("select * from section")
    fun all() : LiveData<List<Section>>
}