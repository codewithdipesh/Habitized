package com.codewithdipesh.habitized.presentation.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import com.codewithdipesh.habitized.domain.model.SubTask
import com.codewithdipesh.habitized.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo : HabitRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(HomeScreenUI())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            selectedDate = LocalDate.now()
        )
        loadHomePage(_uiState.value.selectedDate)
    }

    fun loadHomePage(date: LocalDate){
        viewModelScope.launch(Dispatchers.IO){
            val habits = repo.getHabitsForDay(date)
            val tasks = repo.getTasksForDay(date)
            _uiState.value = _uiState.value.copy(
                habitWithProgressList = habits,
                tasks = tasks
            )
        }
    }

    fun onOptionSelected(option: HomeScreenOption){
        _uiState.value = _uiState.value.copy(
            selectedOption = option
        )
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date
        )
        loadHomePage(date)
    }

    fun toggleDatePicker(){
        _uiState.value = _uiState.value.copy(
            isShowingDatePicker = !_uiState.value.isShowingDatePicker
        )
    }

    suspend fun addUpdateSubTasks(subtasks : List<SubTask>, habitProgressId : UUID){
        //add on the ui first then add on the database

        //change in local first(Ui)
        var updatedList = _uiState.value.habitWithProgressList.toMutableList()
        updatedList = updatedList.map {
            if(it.progress.progressId == habitProgressId){
                it.copy(subtasks = subtasks)
            }else{
                it
            }
        } as MutableList<HabitWithProgress>
        _uiState.value = _uiState.value.copy(
            habitWithProgressList = updatedList
        )
        //change in room
        val previous = repo.getSubtasks(habitProgerssId = habitProgressId )

        val deleted = previous.filter { !subtasks.contains(it) }
        val added = subtasks.filter { !previous.contains(it) }
        val updated = subtasks.filter { previous.contains(it) }

        viewModelScope.launch(Dispatchers.IO) {
            added.forEach {
                repo.insertSubtask(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updated.forEach {
                repo.insertSubtask(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            deleted.forEach {
                repo.deleteSubtask(it.subtaskId)
            }
        }
    }

    fun toggleSubtask(subtask: SubTask){
        val updatedList = _uiState.value.habitWithProgressList.map {habitWithProgress->
            if(habitWithProgress.habit.type == HabitType.Session && habitWithProgress.subtasks.contains(subtask)){
                val updatedSubTasks = habitWithProgress.subtasks.map{
                    if(it.subtaskId == subtask.subtaskId) it.copy(isCompleted = !it.isCompleted) else it
                }
                habitWithProgress.copy(subtasks = updatedSubTasks)
            } else habitWithProgress
        }

        _uiState.value = _uiState.value.copy(
            habitWithProgressList = updatedList
        )
        viewModelScope.launch(Dispatchers.IO) {
            repo.toggleSubTask(subtask.subtaskId)
        }
    }
}