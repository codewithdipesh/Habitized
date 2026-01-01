package com.codewithdipesh.habitized.presentation.navigation

import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitProgress
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed class Screen(val route : String){
    object Welcome : Screen("welcome")
    object OnboardingFirst : Screen("onboarding_first")
    object OnboardingSecond : Screen("onboarding_second")
    object OnboardingThird : Screen("onboarding_third")
    object Home : Screen("home")
    object AddHabit : Screen("addHabit/{id}/{date}") {
        fun createRoute(date: LocalDate = LocalDate.now(),id : String? = ""): String {
            return "addHabit/$id/$date"
        }
    }
    object DurationScreen : Screen("duration/{id}/{title}/{target}/{color}") {
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(habitWithProgress: HabitWithProgress): String {
            val id = habitWithProgress.progress.progressId
            val title = Base64.UrlSafe.encode(habitWithProgress.habit.title.toByteArray())

            val time = habitWithProgress.progress.targetDurationValue
            val hour = time!!.hour
            val minutes = time.minute
            val seconds = time.second
            val target = (hour*3600) +( minutes*60) + seconds

            val color = habitWithProgress.habit.colorKey
            return "duration/$id/$title/$target/$color"
        }
        fun createRoute(id: String, title: String, target: Int, color: String) =
            "duration/$id/$title/$target/$color"
    }
    object SessionScreen : Screen("session_screen/{id}/{title}/{target}/{color}") {
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(habitWithProgress: HabitWithProgress): String {
            val id = habitWithProgress.progress.progressId
            val title = Base64.UrlSafe.encode(habitWithProgress.habit.title.toByteArray())

            val time = habitWithProgress.progress.targetDurationValue
            val hour = time!!.hour
            val minutes = time.minute
            val seconds = time.second
            val target = (hour*3600) +( minutes*60) + seconds

            val color = habitWithProgress.habit.colorKey
            return "session_screen/$id/$title/$target/$color"
        }
    }
    object AddGoal : Screen("addGoal/{id}"){
        fun createRoute(id : String = ""): String {
            return "addGoal/$id"
        }
    }
    object Progress : Screen("progress")
    object MyThoughts : Screen("myThoughts")
    object AddWidget : Screen("addWidget")
    object HabitScreen : Screen("habit_screen/{id}/{title}/{color}"){
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(habit: Habit): String {
            val id = habit.habit_id
            val title = Base64.UrlSafe.encode(habit.title.toByteArray())
            val color = habit.colorKey
            return "habit_screen/$id/$title/$color"
        }
    }
    object HabitShareScreen : Screen("habit_screen/share")
    object GoalScreen : Screen("goal_screen/{id}/{title}"){
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(goal: Goal?): String {
            val id = goal?.id ?: ""
            val title = if(goal != null) Base64.UrlSafe.encode(goal.title.toByteArray())
                else Base64.UrlSafe.encode("Overall Goal".toByteArray())
            return "goal_screen/$id/$title"
        }
    }
    object BackupScreen : Screen("backup")
}
