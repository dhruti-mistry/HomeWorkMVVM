package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.viewbinding.BuildConfig

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}