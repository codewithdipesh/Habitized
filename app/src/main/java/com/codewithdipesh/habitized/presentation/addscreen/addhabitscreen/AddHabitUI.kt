package com.codewithdipesh.habitized.presentation.addscreen.addhabitscreen

import androidx.annotation.ColorRes
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.CountParam
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.ReminderType
import com.codewithdipesh.habitized.presentation.util.DailySelected
import com.codewithdipesh.habitized.presentation.util.Days
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class AddHabitUI(
    val habit_id: UUID? = null,
    val title: String = "",
    val description: String = "",
    val type: HabitType = HabitType.OneTime,
    val isEditMode: Boolean = false,
    val goal_id: UUID? = null,
    val goal_name: String = "",
    val start_date: LocalDate = LocalDate.now(),
    val frequency: Frequency = Frequency.Daily,
    val days_of_week: Map<Days,Boolean> = DailySelected,
    val daysOfMonth: List<Int> = emptyList(),
    val reminderType: ReminderType? = null,
    val is_active: Boolean = true,
    val colorKey : String = "red",
    val countParam : CountParam? = CountParam.Glasses,
    val countTarget:Int? = null,

    val selectedHour : Int = 0,
    val selectedMinute : Int = 45,
    val selectedSeconds : Int = 0,

    val paramOptions: List<CountParam> = CountParam.getParams(type),
    val isShowingParamOptions : Boolean = false,
    val isShowReminderTime : Boolean = false,

    val colorOptions : Map<Int,String> = mapOf(
        R.color.red to "red",
        R.color.blue to "blue",
        R.color.green to "green",
        R.color.yellow to "yellow",
        R.color.purple to "purple",
        R.color.see_green to "see_green"
    ),
    val colorOptionAvailable : Boolean = false,
    val availableGoals : List<Goal> = emptyList()
)
