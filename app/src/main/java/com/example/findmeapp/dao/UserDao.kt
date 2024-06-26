package com.example.findmeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.findmeapp.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}