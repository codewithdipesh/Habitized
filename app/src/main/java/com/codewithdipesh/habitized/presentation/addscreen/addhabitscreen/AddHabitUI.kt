package com.codewithdipesh.habitized.presentation.addscreen.addhabitscreen

import androidx.annotation.ColorRes
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.CountParam
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AddHabitUI(
    val habit_id: UUID? = null,
    val title: String = "",
    val description: String = "",
    val type: HabitType = HabitType.Count,
    val goal_id: UUID? = null,
    val start_date: LocalDate = LocalDate.now(),
    val frequency: Frequency = Frequency.Daily,
    val days_of_week: List<Int> = mutableListOf(1,1,1,1,1,1,1),//days of week
    val daysOfMonth: List<Int>? = null,
    val reminder_time: LocalTime? =null,
    val is_active: Boolean = false,
    @ColorRes val color : Int = R.color.primary,
    val countParam : CountParam? = CountParam.Glasses,
    val countTarget:Int? = null,
    val durationParam: String? = null,
    val duration:Float? = null,

    val paramOptions: List<CountParam> = CountParam.getParams(type),
    val isShowReminderTime : Boolean = false,

    val colorOptions : List<Int> = listOf(
        R.color.red,
        R.color.blue,
        R.color.green,
        R.color.yellow,
        R.color.purple,
        R.color.see_green
    ),
    val colorOptionAvailable : Boolean = false
)
