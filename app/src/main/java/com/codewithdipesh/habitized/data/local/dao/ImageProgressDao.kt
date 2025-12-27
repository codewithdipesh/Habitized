package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codewithdipesh.habitized.data.local.entity.ImageProgressEntity
import com.codewithdipesh.habitized.data.local.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ImageProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageProgress(imageProgressEntity: ImageProgressEntity)

    @Query("SELECT * FROM imageProgress WHERE id = :imageProgressId")
    suspend fun getImageProcess(imageProgressId: UUID): ImageProgressEntity

    @Query("SELECT * FROM imageProgress WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getImageProcessesForHabit(habitId: UUID): List<ImageProgressEntity>

    @Query("DELETE FROM imageProgress WHERE id = :imageProgressId")
    suspend fun deleteImageProgress(imageProgressId: UUID)

    @Query("DELETE FROM imageProgress WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: UUID)

    @Query("SELECT * FROM imageProgress")
    suspend fun getAllImageProgress(): List<ImageProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageProgressEntity: ImageProgressEntity)

    @Query("DELETE FROM imageProgress")
    suspend fun deleteAll()
}
