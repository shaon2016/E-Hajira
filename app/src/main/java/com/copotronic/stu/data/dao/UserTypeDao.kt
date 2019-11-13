package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.copotronic.stu.model.UserType

@Dao
interface UserTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userType: UserType)

    @Query("select * from usertype")
    fun all() : LiveData<List<UserType>>
}