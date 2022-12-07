package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val userId: String,
    val xAcc: String? = null,
    var userName: String? = null,
    var isDeleted: Boolean? = null
)