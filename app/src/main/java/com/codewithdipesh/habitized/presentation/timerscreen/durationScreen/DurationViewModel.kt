package com.codewithdipesh.habitized.presentation.timerscreen.durationScreen

import androidx.lifecycle.ViewModel
import com.codewithdipesh.habitized.data.repository.HabitRepoImpl
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import com.codewithdipesh.habitized.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.concurrent.timer

@HiltViewModel
class DurationViewModel @Inject constructor(
    private val repo : HabitRepository
) : ViewModel(){

     private val _state = MutableStateFlow(DurationUI())
     val state = _state.asStateFlow()

    suspend fun init(id : UUID){
        val response = repo.getHabitProgressById(id)
        _state.value = _state.value.copy(
            progressId = id,
            habitWithProgress = response
        )
    }

    fun resumeTimer(){
        _state.value = _state.value.copy(
            timerState = TimerState.Resumed
        )
    }

    fun pauseTimer(){
        _state.value = _state.value.copy(
            timerState = TimerState.Paused
        )
    }

}