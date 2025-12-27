package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codewithdipesh.habitized.data.local.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SubTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity)

    @Query("SELECT * FROM sub_tasks WHERE habitProgressId = :habitProgressId")
    suspend fun getSubtasksByHabitProgressId(habitProgressId: UUID): List<SubtaskEntity>?

    @Query("DELETE FROM sub_tasks WHERE subtaskId = :subtaskId")
    suspend fun deleteSubtask(subtaskId: UUID)

    @Query("DELETE FROM sub_tasks WHERE habitProgressId = :habitProgressId")
    suspend fun deleteSubtaskByHabitId(habitProgressId: UUID)

    @Query("UPDATE sub_tasks SET isCompleted = not isCompleted WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskCompletion(subtaskId: UUID)

    @Query("SELECT * FROM sub_tasks")
    suspend fun getAllSubtasks(): List<SubtaskEntity>

    @Query("DELETE FROM sub_tasks")
    suspend fun deleteAllSubtasks()
}
