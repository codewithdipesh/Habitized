package com.codewithdipesh.habitized.data.local.mapper

import com.codewithdipesh.habitized.data.local.entity.HabitEntity
import com.codewithdipesh.habitized.domain.model.CountParam
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitType

fun Habit.toEntity(): HabitEntity {
    val days_of_Month = daysOfMonth?.joinToString(",")
    return HabitEntity(
        habit_id = habit_id,
        title = title,
        description = description,
        type = type.displayName,
        goal_id = goal_id,
        start_date = start_date,
        frequency = frequency.displayName,
        days_of_week = days_of_week.joinToString(","),
        daysOfMonth = days_of_Month,
        reminder_time = reminder_time,
        is_active = is_active,
        icon = icon,
        color = color,
        countParam = countParam?.displayName,
        countTarget = countTarget,
        durationParam = durationParam,
        duration = duration
    )
}

fun HabitEntity.toHabit() : Habit {
    return Habit(
        habit_id = habit_id,
        title = title,
        description = description,
        type = HabitType.fromString(type),
        goal_id = goal_id,
        start_date = start_date,
        frequency = Frequency.fromString(frequency),
        days_of_week = days_of_week.split(",").map { it.toInt() },
        daysOfMonth = daysOfMonth?.split(",")?.map{it.toInt()},
        reminder_time = reminder_time,
        is_active = is_active,
        icon = icon,
        color = color,
        countParam = countParam?.let { CountParam.fromString(countParam) },
        countTarget = countTarget,
        durationParam = durationParam,
        duration = duration
    )
}