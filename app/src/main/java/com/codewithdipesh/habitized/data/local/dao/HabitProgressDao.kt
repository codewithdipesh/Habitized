package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codewithdipesh.habitized.data.local.entity.HabitProgressEntity
import com.codewithdipesh.habitized.domain.model.Status
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface HabitProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: HabitProgressEntity)

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId")
    suspend fun getHabitProgress(habitId: UUID): List<HabitProgressEntity>

    @Query("SELECT date FROM habit_progress WHERE habitId = :habitId AND status = :status AND date <= :today ORDER BY date DESC")
    suspend fun getCompletedHabitProgressDates(
        habitId: UUID,
        status: String = Status.Done.displayName,
        today: LocalDate= LocalDate.now()
    ): List<LocalDate>

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId AND status = :status AND date <= :today ORDER BY date DESC")
    suspend fun getCompletedHabitProgress(
        habitId: UUID,
        status: String = Status.Done.displayName,
        today: LocalDate= LocalDate.now()
    ): List<HabitProgressEntity>

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId AND date = :date")
    suspend fun getHabitProgressOfTheDay(habitId: UUID, date: LocalDate): HabitProgressEntity

    @Query("DELETE FROM habit_progress WHERE progressId = :progressId")
    suspend fun deleteProgress(progressId: UUID)

    @Query("DELETE FROM habit_progress WHERE habitId = :habitId")
    suspend fun deleteProgressForHabit(habitId: UUID)

    @Query("SELECT * FROM habit_progress WHERE progressId = :habitProgressId")
    suspend fun getHabitProgressById(habitProgressId: UUID): HabitProgressEntity

    @Query("SELECT * FROM habit_progress WHERE date = :date ")
    fun getAllProgress(date: LocalDate): Flow<List<HabitProgressEntity>>

    @Query("UPDATE habit_progress SET status = :status WHERE progressId = :progressId")
    suspend fun onUpdateStatus(status : String,progressId: UUID)

    @Query("UPDATE habit_progress SET currentCount = :count WHERE progressId = :progressId")
    suspend fun onUpdateCount(count : Int,progressId: UUID)

    @Query("SELECT EXISTS(SELECT 1 FROM habit_progress WHERE habitId = :habitId AND date = :date AND status = :status)")
    suspend fun checkDoneOrNot(habitId: UUID,date: LocalDate,status: String = Status.Done.displayName) : Boolean

    @Query("SELECT * FROM habit_progress")
    suspend fun getAllProgress(): List<HabitProgressEntity>

    @Query("DELETE FROM habit_progress")
    suspend fun deleteAllProgress()
}
