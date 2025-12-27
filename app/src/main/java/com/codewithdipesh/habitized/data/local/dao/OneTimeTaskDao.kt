package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codewithdipesh.habitized.data.local.entity.OneTimeTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface OneTimeTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: OneTimeTaskEntity)

    @Query("SELECT * FROM one_time_tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: UUID): OneTimeTaskEntity?

    @Query("SELECT * FROM one_time_tasks where date = :date ORDER BY reminder_time ASC")
    suspend fun getAllTasks(date: LocalDate): List<OneTimeTaskEntity>

    @Query("DELETE FROM one_time_tasks WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: UUID)

    @Query("UPDATE one_time_tasks SET isCompleted = not isCompleted WHERE taskId = :taskId")
    suspend fun updateTaskCompletion(taskId: UUID)

    @Query("SELECT * FROM one_time_tasks")
    suspend fun getAllTasks(): List<OneTimeTaskEntity>

    @Query("DELETE FROM one_time_tasks")
    suspend fun deleteAllTasks()
}
