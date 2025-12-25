package com.codewithdipesh.habitized.data.repository

import com.codewithdipesh.habitized.data.local.dao.GoalDao
import com.codewithdipesh.habitized.data.local.dao.HabitDao
import com.codewithdipesh.habitized.data.local.dao.HabitProgressDao
import com.codewithdipesh.habitized.data.local.dao.ImageProgressDao
import com.codewithdipesh.habitized.data.local.dao.OneTimeTaskDao
import com.codewithdipesh.habitized.data.local.dao.SubTaskDao
import com.codewithdipesh.habitized.data.local.entity.HabitEntity
import com.codewithdipesh.habitized.data.local.entity.HabitProgressEntity
import com.codewithdipesh.habitized.data.local.mapper.toEntity
import com.codewithdipesh.habitized.data.local.mapper.toGoal
import com.codewithdipesh.habitized.data.local.mapper.toHabit
import com.codewithdipesh.habitized.data.local.mapper.toHabitProgress
import com.codewithdipesh.habitized.data.local.mapper.toImageProgress
import com.codewithdipesh.habitized.data.local.mapper.toOneTimeTask
import com.codewithdipesh.habitized.data.local.mapper.toSubTask
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitProgress
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import com.codewithdipesh.habitized.domain.model.ImageProgress
import com.codewithdipesh.habitized.domain.model.OneTimeTask
import com.codewithdipesh.habitized.domain.model.Status
import com.codewithdipesh.habitized.domain.model.SubTask
import com.codewithdipesh.habitized.domain.repository.HabitRepository
import com.codewithdipesh.habitized.presentation.util.getWeekDayIndex
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

class HabitRepoImpl(
    private val habitDao: HabitDao,
    private val habitProgressDao: HabitProgressDao,
    private val oneTimeTaskDao: OneTimeTaskDao,
    private val subtaskDao: SubTaskDao,
    private val goalDao: GoalDao,
    private val imageProgressDao: ImageProgressDao
) : HabitRepository {
    override suspend fun getAllHabits(): List<Habit>{
        val habits = habitDao.getAllHabits()
            .map {
                it.toHabit()
            }
        return habits
    }

    override suspend fun getAllExistingHabits(goalId:UUID?): List<Habit>{
        val habits =  habitDao.getAllHabits()
            .filter { it.goal_id == null || it.goal_id == goalId }
            .map { it.toHabit() }

        return habits
    }

    override suspend fun addOrUpdateHabit(habit: Habit) {
        val existingHabit = habit.habit_id?.let { habitDao.getHabitById(it) }
        if (existingHabit != null) {
            // Update existing habit - preserves foreign key relations (progress, subtasks, etc.)
            habitDao.updateHabit(habit.toEntity())
        } else {
            // Insert new habit
            habitDao.insertHabit(habit.toEntity())
        }
    }
    override suspend fun deleteHabit(habitId: UUID) {
        habitDao.deleteHabit(habitId)
    }
    override suspend fun getHabitById(habitId: UUID): Habit? {
        val habit = habitDao.getHabitById(habitId)
        return if(habit != null){
            habit.toHabit()
        }else{
            null
        }
    }

    override suspend fun getHabitsByGoal(goalId: UUID): List<Habit> {
        return habitDao.getHabitsByGoal(goalId).map { it.toHabit() }
    }

    override suspend fun getHabitsByGoalForDate(
        goalId: UUID,
        date: LocalDate
    ): List<Habit> {
        val habits = habitDao.getHabitsForTheDay(date)
        return habits.filter { it.goal_id == goalId }.map { it.toHabit() }
    }

    override suspend fun getAllHabitsForDate(date: LocalDate): List<Habit> {
        return habitDao.getHabitsForTheDay(date).map { it.toHabit() }
    }

    override suspend fun getOverAllStartDate(): LocalDate {
        return habitDao.getOverAllStartDay()
    }

    override suspend fun updateStreak(
        habitId: UUID,
        current: Int,
        max: Int
    ) {
        habitDao.updateStreak(habitId,current,max)
    }

    override suspend fun unLinkGoalFromHabit(habitId: UUID) {
        habitDao.removeGoalFromHabit(habitId)
    }


    override suspend fun addHabitProgresses() {
        //get all habits-> loop for next 10 days ->
        //check every habits if not exist then create
        val habits  = habitDao.getAllHabits()
    }
    override suspend fun updateHabitProgress(progress: HabitProgress) {
        TODO("Not yet implemented")
    }

    override suspend fun getHabitProgress(
        habitId: UUID,
        date: String
    ): HabitProgress? {
        TODO("Not yet implemented")
    }

    override suspend fun getHabitProgressById(habitProgressId: UUID): HabitWithProgress {
        val response =  habitProgressDao.getHabitProgressById(habitProgressId)
        val habit = habitDao.getHabitById(response.habitId)
            ?: throw IllegalStateException("Habit not found for progress $habitProgressId")
        val subtasks = subtaskDao.getSubtasksByHabitProgressId(habitProgressId)
        return HabitWithProgress(
            habit = habit.toHabit(),
            progress = response.toHabitProgress(),
            date = response.date,
            subtasks = subtasks?.map { it.toSubTask() } ?: emptyList()
        )
    }

    override suspend fun getAllHabitProgress(habitId: UUID): List<HabitProgress>? {
        return habitProgressDao.getHabitProgress(habitId).map { it.toHabitProgress() }
    }

    override suspend fun addTodayHabitProgresses(habits : List<HabitEntity>,date: LocalDate) {
        habits.forEach { habit->
            habitProgressDao.insertProgress(
                HabitProgressEntity(
                     habitId = habit.habit_id,
                     date = date,
                     type = habit.type,
                     countParam = habit.countParam,
                     currentCount = 0,
                     targetCount = habit.countTarget,
                     targetDurationValue = habit.duration,
                     currentSessionNumber = if(habit.type == HabitType.Session.toString()) 0 else null,
                     targetSessionNumber = if(habit.type == HabitType.Session.toString()) habit.countTarget else null,
                     status  = Status.NotStarted.toString(),
                     notes = null,
                     excuse = null
                )
            )
        }
    }

    override suspend fun onDoneHabitProgress(progressId: UUID) {
        habitProgressDao.onUpdateStatus(
            status = Status.Done.toString(),
            progressId = progressId
        )
    }

    override suspend fun onSkipHabitProgress(progressId: UUID) {
        habitProgressDao.onUpdateStatus(
            status = Status.Cancelled.toString(),
            progressId = progressId
        )
    }

    override suspend fun onNotStartedHabitProgress(progressId: UUID) {
        habitProgressDao.onUpdateStatus(
            status = Status.NotStarted.toString(),
            progressId = progressId
        )
    }

    override suspend fun onStartedHabitProgress(progressId: UUID) {
        habitProgressDao.onUpdateStatus(
            status = Status.Ongoing.toString(),
            progressId = progressId
        )
    }

    override suspend fun onUpdateCounterHabitProgress(count:Int,progressId: UUID) {
        habitProgressDao.onUpdateCount(count,progressId)
    }

    override suspend fun deleteHabitProgressForHabit(habitId: UUID) {
        habitProgressDao.deleteProgressForHabit(habitId)
    }

    override suspend fun checkHabitDoneOrNot(
        habitId: UUID,
        date: LocalDate
    ): Boolean {
        return habitProgressDao.checkDoneOrNot(habitId,date)
    }

    override suspend fun getAllCompletedDates(habitId: UUID): List<LocalDate> {
        return  habitProgressDao.getCompletedHabitProgressDates(habitId)
    }

    override suspend fun getAllCompletedProgress(habitId: UUID): List<HabitProgress> {
        return habitProgressDao.getCompletedHabitProgress(habitId).map { it.toHabitProgress() }
    }


    override suspend fun addGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }
    override suspend fun updateGoal(goal: Goal) {
        TODO("Not yet implemented")
    }
    override suspend fun deleteGoal(goalId: UUID) {
        goalDao.deleteGoal(goalId)
    }
    override suspend fun getGoalById(goalId: UUID): Goal? {
        val habits = habitDao.getHabitsByGoal(goalId)
        return goalDao.getGoalById(goalId)?.toGoal(habits.map { it.toHabit() })
    }
    override suspend fun getAllGoals(): List<Goal> {
       return goalDao.getAllGoals().map{
           it.toGoal(emptyList())
       }
    }

    override suspend fun getExistingGoals(): List<Goal> {
        return goalDao.getAllGoals().map { it.toGoal(emptyList()) }
    }


    override suspend fun addOneTimeTask(task: OneTimeTask) {
        oneTimeTaskDao.insertTask(task.toEntity())
    }
    override suspend fun updateOneTimeTask(task: OneTimeTask) {
        oneTimeTaskDao.insertTask(task.toEntity())
    }
    override suspend fun deleteOneTimeTask(taskId: UUID) {
        oneTimeTaskDao.deleteTask(taskId)
    }

    override suspend fun toggleOneTimeTask(taskId: UUID) {
        oneTimeTaskDao.updateTaskCompletion(taskId)
    }


    override suspend fun insertSubtask(subtask: SubTask) {
        subtaskDao.insertSubtask(subtask.toEntity())
    }

    override suspend fun getSubtasks(habitProgerssId: UUID): List<SubTask> {
        val result =  subtaskDao.getSubtasksByHabitProgressId(habitProgerssId)

        return result?.map { it.toSubTask() } ?: emptyList()
    }

    override suspend fun updateSubtask(subtask: SubTask) {
        TODO("Not yet implemented")
    }
    override suspend fun deleteSubtask(subtaskId: UUID) {
        subtaskDao.deleteSubtask(subtaskId)
    }
    override suspend fun toggleSubTask(subtaskId: UUID) {
        subtaskDao.updateSubtaskCompletion(subtaskId)
    }

    override suspend fun insertImageProgress(imageProcess: ImageProgress) {
        imageProgressDao.insertImageProgress(imageProcess.toEntity())
    }

    override suspend fun getImageProgress(imageProgressId: UUID): ImageProgress {
        return imageProgressDao.getImageProcess(imageProgressId).toImageProgress()
    }

    override suspend fun getImageProgressesForHabit(habitId: UUID): List<ImageProgress> {
        return imageProgressDao.getImageProcessesForHabit(habitId).map { it.toImageProgress() }
    }

    override suspend fun deleteImageProgress(imageProgressId: UUID) {
        imageProgressDao.deleteImageProgress(imageProgressId)
    }


    override suspend fun getHabitsForDay(date: LocalDate): List<HabitWithProgress> {
        var habits = habitDao.getHabitsForTheDay(date)
        //filtering all habits for weekday wise and then monthday wise
        habits = habits
            .filter{ _habit ->
                _habit.toHabit().daysOfMonth?.contains(date.dayOfMonth) == true || //for days of month
                _habit.toHabit().days_of_week.get(getWeekDayIndex(date.dayOfWeek)) == 1 || //for weekdays( e.g :- only sunday,wed)
                _habit.frequency == Frequency.Daily.toString() //all others habit daily frequency
            }
        //create all the progress
        (0 until 7).map{date.plusDays(it.toLong())}.forEach {
            addTodayHabitProgresses(habits,date)
        }

        return habits.map { habit ->
            val progress = habitProgressDao.getHabitProgressOfTheDay(habit.habit_id, date)
            val subtasks = if (progress != null) {
                subtaskDao.getSubtasksByHabitProgressId(progress.progressId)?.map { it.toSubTask() } ?: emptyList()
            } else {
                emptyList()
            }

            HabitWithProgress(
                habit = habit.toHabit(),
                date = date,
                progress = progress.toHabitProgress(),
                subtasks = subtasks
            )
        }
    }


    override suspend  fun getTasksForDay(date: LocalDate): List<OneTimeTask> {
        return oneTimeTaskDao.getAllTasks(date)
            .map {it.toOneTimeTask()}
    }


}