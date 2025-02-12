package com.example.greenstep.roomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel)

    @Query("SELECT * FROM user_table")
    fun getUser(): kotlinx.coroutines.flow.Flow<List<UserModel>>

    @Query("DELETE FROM user_table")
    suspend fun deleteUser()
}