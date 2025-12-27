package com.codewithdipesh.habitized.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codewithdipesh.habitized.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE goal_id = :goalId")
    suspend fun getGoalById(goalId: UUID): GoalEntity?

    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<GoalEntity>

    @Query("DELETE FROM goals WHERE goal_id = :goalId")
    suspend fun deleteGoal(goalId: UUID)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}

