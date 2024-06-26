package com.example.findmeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users" ,indices = [androidx.room.Index(value = ["number"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String
)
