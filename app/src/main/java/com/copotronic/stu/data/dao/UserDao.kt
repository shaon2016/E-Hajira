package com.copotronic.stu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.copotronic.stu.model.User

@Dao
interface UserDao {


    @Query("select * from user")
    fun all() : LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(u: User)
}