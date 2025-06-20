package com.codewithdipesh.habitized.domain.repository
import com.codewithdipesh.habitized.data.local.entity.HabitEntity
import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitProgress
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import com.codewithdipesh.habitized.domain.model.OneTimeTask
import com.codewithdipesh.habitized.domain.model.SubTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

interface HabitRepository {

    // Habits
    suspend fun addOrUpdateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: UUID)
    suspend fun getHabitById(habitId: UUID): Habit?

    // Habit Progress
    suspend fun addHabitProgresses()
    suspend fun updateHabitProgress(progress: HabitProgress)
    suspend fun getHabitProgress(habitId: UUID, date: String): HabitProgress?
    suspend fun getHabitProgressById(habitProgressId: UUID): HabitWithProgress
    suspend fun getAllHabitProgress(habitId: UUID): List<HabitProgress>?
    suspend fun addTodayHabitProgresses(habits : List<HabitEntity>,date : LocalDate)
    suspend fun onDoneHabitProgress(progressId: UUID)
    suspend fun onSkipHabitProgress(progressId: UUID)
    suspend fun onNotStartedHabitProgress(progressId: UUID)
    suspend fun onStartedHabitProgress(progressId: UUID)
    suspend fun onUpdateCounterHabitProgress(count :Int,progressId: UUID)

    // Goals
    suspend fun addGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: UUID)
    suspend fun getGoalById(goalId: UUID): Flow<Goal?> //sending habits also with it
    fun getAllGoals(): Flow<List<Goal>>

    // One-Time Tasks
    suspend fun addOneTimeTask(task: OneTimeTask)
    suspend fun updateOneTimeTask(task: OneTimeTask)
    suspend fun deleteOneTimeTask(taskId: UUID)


    // Subtasks
    suspend fun insertSubtask(subtask: SubTask)
    suspend fun getSubtasks(habitProgerssId: UUID) : List<SubTask>
    suspend fun updateSubtask(subtask: SubTask)
    suspend fun deleteSubtask(subtaskId: UUID)
    suspend fun toggleSubTask(subtaskId: UUID)

    suspend fun getHabitsForDay(date: LocalDate): List<HabitWithProgress>
    suspend fun getTasksForDay(date: LocalDate): List<OneTimeTask>
}
