package com.example.simplenote.data.local

import androidx.room.*
import com.example.simplenote.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<User?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
} 