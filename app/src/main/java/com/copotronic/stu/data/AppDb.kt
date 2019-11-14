package com.copotronic.stu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.copotronic.stu.data.dao.*
import com.copotronic.stu.model.*

@Database(
    entities = [
        User::class,
        Institute::class,
        About::class,
        Department::class,
        Designation::class,
        Section::class,
        Shift::class,
        UserType::class

    ], version = 2, exportSchema = false
)
abstract class AppDb : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun instituteDao(): InstituteDao
    abstract fun aboutDao(): AboutDao
    abstract fun deptDao(): DeptDao
    abstract fun designationDao(): DesignationDao
    abstract fun sectionDao(): SectionDao
    abstract fun shiftDao(): ShiftDao
    abstract fun userTypeDao(): UserTypeDao


    companion object {
        private val DB_NAME = "appdb.db"

        private var instance: AppDb? = null

        @Synchronized
        fun getInstance(context: Context): AppDb? {
            if (instance == null)
                instance = create(context.applicationContext)

            return instance
        }

        private fun create(context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}