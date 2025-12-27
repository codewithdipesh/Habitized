package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codewithdipesh.habitized.data.local.entity.HabitEntity
import com.codewithdipesh.habitized.domain.model.Frequency
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE habit_id = :habitId")
    suspend fun getHabitById(habitId: UUID): HabitEntity?

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<HabitEntity>

    @Query("DELETE FROM habits WHERE habit_id = :habitId")
    suspend fun deleteHabit(habitId: UUID)

    @Query("SELECT * FROM habits WHERE start_date <= :date")
    suspend fun getHabitsForTheDay(date: LocalDate): List<HabitEntity>

    @Query(
        """
        UPDATE habits 
        SET is_active = :isActive 
        WHERE habit_id = :habitId
    """
    )
    suspend fun updateHabitStatus(habitId: UUID, isActive: Boolean)

    @Query(
        """
        UPDATE habits 
        SET currentStreak = :current , maxStreak = :max
        WHERE habit_id = :habitId
    """
    )
    suspend fun updateStreak(habitId: UUID, current: Int, max: Int)

    @Query("SELECT * FROM habits WHERE goal_id = :goalId")
    suspend fun getHabitsByGoal(goalId: UUID): List<HabitEntity>

    @Query("SELECT MIN(start_date) FROM habits")
    suspend fun getOverAllStartDay(): LocalDate

    @Query("UPDATE habits SET goal_id = NULL WHERE habit_id = :habitId")
    suspend fun removeGoalFromHabit(habitId: UUID)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}
