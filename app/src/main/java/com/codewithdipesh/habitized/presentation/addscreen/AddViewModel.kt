package com.codewithdipesh.habitized.presentation.addscreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.habitized.domain.alarmManager.AlarmItem
import com.codewithdipesh.habitized.domain.alarmManager.AlarmScheduler
import com.codewithdipesh.habitized.domain.model.CountParam
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.domain.model.Habit
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.ReminderType
import com.codewithdipesh.habitized.domain.repository.HabitRepository
import com.codewithdipesh.habitized.domain.util.getNextAlarmDateTime
import com.codewithdipesh.habitized.presentation.addscreen.addGoalScreen.AddGoalUI
import com.codewithdipesh.habitized.presentation.addscreen.addhabitscreen.AddHabitUI
import com.codewithdipesh.habitized.presentation.util.Days
import com.codewithdipesh.habitized.presentation.util.IntToWeekDayMap
import com.codewithdipesh.habitized.presentation.util.WeekDayMapToInt
import com.codewithdipesh.habitized.presentation.util.toDaysOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import kotlin.math.absoluteValue

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo : HabitRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel(){

    private val _habitUiState = MutableStateFlow(AddHabitUI())
    val habitUiState = _habitUiState.asStateFlow()

    private val _goalUiState = MutableStateFlow(AddGoalUI())
    val goalUiState = _goalUiState.asStateFlow()

    private val _uiEvent = Channel<String>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    private var lastEmitted =""
    private var clearJob : Job? = null

    //habit
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun addHabit(date : LocalDate){
        if(checkSavability()){
            //save habit in database
            val habit = Habit(
                habit_id = _habitUiState.value.habit_id,
                title = _habitUiState.value.title,
                description = _habitUiState.value.description,
                type = _habitUiState.value.type,
                goal_id = _habitUiState.value.goal_id,
                start_date = if(_habitUiState.value.habit_id != null) _habitUiState.value.start_date else date,
                frequency = _habitUiState.value.frequency,
                days_of_week = if( _habitUiState.value.frequency == Frequency.Weekly) WeekDayMapToInt( _habitUiState.value.days_of_week)
                else mutableListOf(0,0,0,0,0,0,0),
                daysOfMonth = _habitUiState.value.daysOfMonth,
                reminderType = if(_habitUiState.value.isShowReminderTime) _habitUiState.value.reminderType else null,
                is_active = _habitUiState.value.is_active,
                colorKey = _habitUiState.value.colorKey,
                countParam = _habitUiState.value.countParam,
                countTarget = _habitUiState.value.countTarget,
                duration = LocalTime.of(
                    _habitUiState.value.selectedHour,
                    _habitUiState.value.selectedMinute,
                    _habitUiState.value.selectedSeconds,

                    )
            )
            repo.addOrUpdateHabit(habit)
            sendEvent("Habit Created Successfully")


            //schedule alarm
            if(_habitUiState.value.isShowReminderTime){
                val result = getNextAlarmDateTime(
                    date = date,
                    now = LocalDateTime.of(date, LocalTime.now()),
                    frequency = _habitUiState.value.frequency,
                    reminderType = _habitUiState.value.reminderType!!,
                    daysOfWeek = _habitUiState.value.days_of_week, // Map<Days, Boolean>
                    daysOfMonth = _habitUiState.value.daysOfMonth
                )
                if(!result.error){
                    val alarmItem = AlarmItem(
                        id = _habitUiState.value.habit_id!!,
                        reminderType = _habitUiState.value.reminderType!!,
                        nextAlarmDateTime = result.nextDateTime,
                        text = getDeterministicNotificationMessage(_habitUiState.value.habit_id!!), // Use habitId here
                        title = "Its time to do ${_habitUiState.value.title}",
                        frequency = _habitUiState.value.frequency,
                        daysOfWeek = WeekDayMapToInt( _habitUiState.value.days_of_week),
                        daysOfMonth = _habitUiState.value.daysOfMonth
                    )
                    alarmScheduler.schedule(alarmItem)
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun init(id: UUID){
        val habit = repo.getHabitById(id)
        habit?.let {
            _habitUiState.value = _habitUiState.value.copy(
                habit_id = id,
                title = habit.title,
                description = habit.description ?: "",
                goal_id = habit.goal_id,
                start_date = habit.start_date,
                frequency = habit.frequency,
                days_of_week = IntToWeekDayMap(habit.days_of_week),
                daysOfMonth = habit.daysOfMonth ?: emptyList(),
                reminderType = habit.reminderType,
                is_active = habit.is_active,
                isShowReminderTime = habit.reminderType != null,
                colorKey = habit.colorKey,
                countParam = habit.countParam,
                countTarget = habit.countTarget,
                selectedHour = habit.duration?.hour ?: 0,
                selectedMinute = habit.duration?.minute ?: 0,
                selectedSeconds = habit.duration?.second ?: 0,
                paramOptions = CountParam.getParams(habit.type),
                isEditMode = true,
            )
            setType(habit.type)
        }
        if(habit?.goal_id != null){
            val goal = repo.getGoalById(habit.goal_id)
            _habitUiState.value = _habitUiState.value.copy(
                goal_name = goal!!.title
            )
        }
    }

    suspend fun getGoals(){
        val goals = repo.getExistingGoals()
        _habitUiState.value = _habitUiState.value.copy(
            availableGoals = goals
        )
    }

    fun setGoal(goal:Goal?){
        goal?.let {
            _habitUiState.value = _habitUiState.value.copy(
                goal_id = goal.id,
                goal_name = goal.title
            )
        }
        if(goal == null){
            _habitUiState.value = _habitUiState.value.copy(
                goal_id = null,
                goal_name = ""
            )
        }
    }

    fun setType(type: HabitType){
        _habitUiState.value = _habitUiState.value.copy(
            type = type,
            paramOptions = CountParam.getParams(type),
            countParam = if(type == HabitType.OneTime) null
            else CountParam.getParams(type).first()
        )
    }

    fun setParam(param: CountParam){
        _habitUiState.value = _habitUiState.value.copy(
            countParam = param
        )
    }
    fun setTargetCount(count: Int){
        _habitUiState.value = _habitUiState.value.copy(
            countTarget = count
        )
    }
    fun setHours(duration: Int){
        _habitUiState.value = _habitUiState.value.copy(
            selectedHour = duration
        )
    }
    fun setMinutes(duration: Int){
        _habitUiState.value = _habitUiState.value.copy(
            selectedMinute = duration
        )
    }
    fun setSeconds(duration: Int){
        _habitUiState.value = _habitUiState.value.copy(
            selectedSeconds = duration
        )
    }

    fun setColor(color: String){
        _habitUiState.value = _habitUiState.value.copy(
            colorKey = color
        )
    }

    fun setFrequency(frequency: Frequency){
        _habitUiState.value = _habitUiState.value.copy(
            frequency = frequency
        )
    }

    fun toggleReminderOption(){
        val newShowReminderTime = !_habitUiState.value.isShowReminderTime

        _habitUiState.value = _habitUiState.value.copy(
            isShowReminderTime = newShowReminderTime,
            reminderType = if(newShowReminderTime){
                ReminderType.Once( LocalTime.now())
            }else{
                null
            }
        )
    }

    fun setReminderType(reminderType: ReminderType){
        if(_habitUiState.value.isShowReminderTime){
            _habitUiState.value = _habitUiState.value.copy(
                reminderType = reminderType
            )
        }
    }

    fun setReminderTime(time : LocalTime){
        if(_habitUiState.value.reminderType != null && _habitUiState.value.reminderType is ReminderType.Once){
           _habitUiState.value = _habitUiState.value.copy(
               reminderType = ReminderType.Once(time)
           )
        }
    }

    fun setIntervalReminder(interval : Int){
        if(_habitUiState.value.reminderType != null && _habitUiState.value.reminderType is ReminderType.Interval){
            _habitUiState.value = _habitUiState.value.copy(
                reminderType = ReminderType.Interval(
                    interval = interval,
                    fromTime = (_habitUiState.value.reminderType as ReminderType.Interval).fromTime,
                    toTime = (_habitUiState.value.reminderType as ReminderType.Interval).toTime
                )
            )
        }
        Log.d("AddViewModel", "setIntervalReminder: ${_habitUiState.value.reminderType}")
    }
    fun setIntervalFrom(time : LocalTime){
        if(_habitUiState.value.reminderType != null && _habitUiState.value.reminderType is ReminderType.Interval){
            _habitUiState.value = _habitUiState.value.copy(
                reminderType = ReminderType.Interval(
                    interval = (_habitUiState.value.reminderType as ReminderType.Interval).interval,
                    fromTime = time,
                    toTime = (_habitUiState.value.reminderType as ReminderType.Interval).toTime
                )
            )
        }
    }
    fun setIntervalTo(time : LocalTime){
        if(_habitUiState.value.reminderType != null && _habitUiState.value.reminderType is ReminderType.Interval){
            _habitUiState.value = _habitUiState.value.copy(
                reminderType = ReminderType.Interval(
                    interval = (_habitUiState.value.reminderType as ReminderType.Interval).interval,
                    fromTime = (_habitUiState.value.reminderType as ReminderType.Interval).fromTime,
                    toTime = time
                )
            )
        }
    }

    fun setHabitTitle(title : String){
        _habitUiState.value = _habitUiState.value.copy(
            title = title
        )
    }

    fun setHabitDescription(description : String){
        _habitUiState.value = _habitUiState.value.copy(
            description = description
        )
    }
    fun toggleHabitColorOption(){
        _habitUiState.value = _habitUiState.value.copy(
            colorOptionAvailable = !_habitUiState.value.colorOptionAvailable
        )
    }

    fun toggleParamOption(){
        _habitUiState.value = _habitUiState.value.copy(
            isShowingParamOptions = !_habitUiState.value.isShowingParamOptions
        )
    }

    fun onSelectWeekday(day: Days) {
        val currentMap = _habitUiState.value.days_of_week.toMutableMap()
        val currentValue = currentMap[day] ?: false
        currentMap[day] = !currentValue  // Toggle the value

        _habitUiState.value = _habitUiState.value.copy(
            days_of_week = currentMap
        )
    }

    fun onSelectDayofMonth(day: Int) {
        val currentList = _habitUiState.value.daysOfMonth.toMutableList()

        if(currentList.contains(day)){
            currentList.remove(day)
        }else{
            currentList.add(day)
        }
        _habitUiState.value = _habitUiState.value.copy(
            daysOfMonth = currentList
        )
    }

    private fun checkSavability() :  Boolean{
        val state = _habitUiState.value
        if(state.title == ""){
            sendEvent("Title cannot be empty")
            return false
        }
        when(state.type){
            HabitType.Count -> {
                if(state.countTarget == null || state.countTarget < 1){
                    sendEvent("Target should be greater than 0")
                    return false
                }
            }
            HabitType.Duration -> {
                if(state.selectedHour == 0 && state.selectedMinute == 0 && state.selectedSeconds == 0){
                    sendEvent("Duration cannot be zero")
                    return false
                }
            }
            HabitType.OneTime -> {}
            HabitType.Session -> {
                if(state.countTarget == null || state.countTarget < 1){
                    sendEvent("Target should be greater than 0")
                    return false
                }
                if(state.selectedHour == 0 && state.selectedMinute == 0 && state.selectedSeconds == 0){
                    sendEvent("Duration cannot be zero")
                    return false
                }
            }
        }
        when(state.frequency){
            is Frequency.Weekly ->{
                if(state.days_of_week.values.all { !it }){
                    sendEvent("Select atleast one day")
                    return false
                }
            }
            is Frequency.Monthly -> {
                if(state.daysOfMonth == emptyList<Int>()){
                    sendEvent("Select atleast one day")
                    return false
                }
            }
            else -> {}
        }
        if(state.reminderType != null){
            when(state.reminderType){
                is ReminderType.Once -> {}
                is ReminderType.Interval -> {
                    if(state.reminderType.fromTime >= state.reminderType.toTime){
                        sendEvent("start time should be before end time")
                        return false
                    }
                    if(state.reminderType.interval < 1){
                        sendEvent("Interval should be greater")
                        return false
                    }
                }
            }
        }
        //everything is okay
        return true
    }

    private fun sendEvent(message: String) {
        if(lastEmitted != message){
            viewModelScope.launch {
                _uiEvent.send(message)
            }
            lastEmitted = message

            clearJob?.cancel()
            clearJob = viewModelScope.launch {
                delay(5000)
                lastEmitted =""
            }
        }
    }

    fun clearHabitUI(){
        _habitUiState.value = AddHabitUI()
    }

    //goal
    suspend fun initGoal(id :UUID){
        val goal = repo.getGoalById(id)
        goal?.let{
            val habits = repo.getHabitsByGoal(goal.id)
            _goalUiState.value = _goalUiState.value.copy(
                goal_id = goal.id,
                title = goal.title,
                description = goal.description ?: "",
                target_date = goal.target_date,
                start_date = goal.start_date!!,
                isTargetDateVisible = goal.target_date != null,
                habits = habits,
                prevHabits = habits
            )

        }

    }
    fun setGoalTitle(title : String){
        _goalUiState.value = _goalUiState.value.copy(
            title = title
        )
    }
    fun setGoalDescription(description : String){
        _goalUiState.value = _goalUiState.value.copy(
            description = description
        )
    }
    fun toggleGoalTargetDateOption(){
        _goalUiState.value = _goalUiState.value.copy(
            isTargetDateVisible = !_goalUiState.value.isTargetDateVisible
        )
    }
    suspend fun getExistingHabits(id : UUID? = null){
        val habits = repo.getAllExistingHabits(id)
        _goalUiState.value =_goalUiState.value.copy(
            availableHabits = habits
        )
    }
    fun setHabitsAttachedWithGoal(newHabits : List<Habit>){
        val updatedHabits = newHabits.map {
            it.copy(
                goal_id = _goalUiState.value.goal_id
            )
        }
        _goalUiState.value = _goalUiState.value.copy(
            habits = updatedHabits
        )
    }
    fun setTargetDate(date : LocalDate){
        _goalUiState.value = _goalUiState.value.copy(
            target_date = date
        )
    }
    suspend fun addGoal(){
        if(_goalUiState.value.title == ""){
            sendEvent("Title cannot be empty")
            return
        }
        repo.addGoal(
            Goal(
                id = _goalUiState.value.goal_id,
                title = _goalUiState.value.title,
                description = _goalUiState.value.description,
                target_date = _goalUiState.value.target_date,
                start_date = _goalUiState.value.start_date,
                habits = _goalUiState.value.habits
            )
        )
        _goalUiState.value.habits.forEach {
            repo.addOrUpdateHabit(it)
        }
        _goalUiState.value.prevHabits //for removing any habit from goal
            .filter { !_goalUiState.value.habits.contains(it) }
            .forEach {
                repo.addOrUpdateHabit(it.copy(
                    goal_id = null
                ))
            }
        sendEvent("Goal Created Successfully")
    }
    fun clearGoalUI(){
        _goalUiState.value = AddGoalUI()
    }

    private fun getDeterministicNotificationMessage(habitId: UUID): String {
        val messages = listOf(
            "🔥 Streak check! Don’t break it now",
            "One more tap to greatness 📊",
            "Another day, another step closer 🪜",
            "You’re on a roll! Don’t stop the momentum 🌀",
            "Finish strong, legend 💥"
        )
        // Use the habitId's hash to always pick the same message for this habit
        val index = (habitId.hashCode().absoluteValue) % messages.size
        return messages[index]
    }


}