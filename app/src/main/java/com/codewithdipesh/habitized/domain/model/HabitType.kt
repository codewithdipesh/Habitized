package com.codewithdipesh.habitized.domain.model

sealed class HabitType(val displayName: String) {
    object OneTime : HabitType("Task")
    object Count : HabitType("Repeats")
    object Duration : HabitType("Timer")
    object Session : HabitType("Session")

    override fun toString(): String = displayName

    companion object {
        fun fromString(value: String): HabitType {
            return when (value.lowercase()) {
                "repeats","count" -> Count
                "timer","duration" -> Duration
                "session", -> Session
                "task","onetime" -> OneTime
                else -> Count
            }
        }
        fun getHabitTypes(): List<HabitType> {
            return listOf(OneTime, Count, Duration, Session)
        }
    }
}



