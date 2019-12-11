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

    @Query("select * from user where id = :id")
    fun user(id: Int): User?

    @Query("select * from user where left_finger_base64 = :fingerRawDataInStr or right_finger_base64 = :fingerRawDataInStr")
    fun findUserByFinger(fingerRawDataInStr: String) : User?

    @Query("select * from user where user_id = :userId")
    fun findUserByUserId(userId: String) : User?
}