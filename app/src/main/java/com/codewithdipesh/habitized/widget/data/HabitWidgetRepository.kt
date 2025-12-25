package com.codewithdipesh.habitized.widget.data

import com.codewithdipesh.habitized.data.local.dao.HabitDao
import com.codewithdipesh.habitized.data.local.dao.HabitProgressDao
import com.codewithdipesh.habitized.data.local.mapper.toHabit
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.Status
import com.codewithdipesh.habitized.widget.WidgetType
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class HabitWidgetRepository(
    private val habitDao: HabitDao,
    private val progressDao: HabitProgressDao
){

    suspend fun getHabit(habitId : UUID, type : WidgetType) : HabitWidgetInfo? {
        val habit = habitDao.getHabitById(habitId) ?: return null
        val progresses = progressDao.getHabitProgress(habitId)
        return when(type){
            WidgetType.Weekly -> {
                val selectedDates = getWeekDateRange()
                val weeklyProgresses = selectedDates.map {
                    ProgressWidgetData(
                        date = it,
                        status = HabitToWidgetStatus(
                            Status.Companion.fromString(
                                progresses
                                    .find { progress -> progress.date == it }
                                    ?.status ?: Status.NotStarted.toString()
                            )
                        )
                    )
                }
                HabitWidgetInfo(
                    habitId = habitId,
                    name = habit.title,
                    colorKey = habit.colorKey,
                    frequency = type,
                    currentStreak = habit.currentStreak,
                    progress = weeklyProgresses
                )
            }
            WidgetType.Overall -> {
                val selectedDates = getMonthDateRange()
                val monthlyProgresses = selectedDates.map {
                    ProgressWidgetData(
                        date = it,
                        status = HabitToWidgetStatus(
                            Status.Companion.fromString(
                                progresses
                                    .find { progress -> progress.date == it }
                                    ?.status ?: Status.NotStarted.toString()
                            )
                        )
                    )
                }
                HabitWidgetInfo(
                    habitId = habitId,
                    name = habit.title,
                    colorKey = habit.colorKey,
                    frequency = type,
                    currentStreak = habit.currentStreak,
                    progress = monthlyProgresses
                )
            }
        }

    }
    suspend fun getAllHabits() : List<Habit> {
        return habitDao.getAllHabits().map { it.toHabit() }
    }
    private fun getWeekDateRange() : List<LocalDate> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        return (0..6).map { offset ->
            startOfWeek.plusDays(offset.toLong())
        }
    }
    private fun getMonthDateRange() : List<LocalDate> {
        val today = LocalDate.now()
        return (0..(77 + today.dayOfWeek.value) )
            .map{ offset ->
                today.minusDays(offset.toLong())
            }
            .reversed()

    }

}