package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Demo")
data class User constructor(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "name") var name: String
)