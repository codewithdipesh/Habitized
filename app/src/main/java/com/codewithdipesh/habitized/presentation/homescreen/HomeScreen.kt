package com.codewithdipesh.habitized.presentation.homescreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.HabitWithProgress
import com.codewithdipesh.habitized.domain.model.Status
import com.codewithdipesh.habitized.presentation.homescreen.component.AddSubTask
import com.codewithdipesh.habitized.presentation.homescreen.component.AddingOption
import com.codewithdipesh.habitized.presentation.homescreen.component.BottomNavBar
import com.codewithdipesh.habitized.presentation.homescreen.component.CountUpdater
import com.codewithdipesh.habitized.presentation.homescreen.component.DatePicker
import com.codewithdipesh.habitized.presentation.homescreen.component.HabitCard
import com.codewithdipesh.habitized.presentation.homescreen.component.OptionSelector
import com.codewithdipesh.habitized.presentation.homescreen.component.RunningTimer
import com.codewithdipesh.habitized.presentation.homescreen.component.SkipAlertDialog
import com.codewithdipesh.habitized.presentation.homescreen.component.TodoEditor
import com.codewithdipesh.habitized.presentation.navigation.Screen
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular
import com.codewithdipesh.habitized.data.sharedPref.HabitPreference
import com.codewithdipesh.habitized.presentation.homescreen.introscreen.IntroDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewmodel: HomeViewModel,
    drawerState: DrawerState,
    habitPreference: HabitPreference? = null,
    introVideoUrl: String = ""
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var previousScrollOffset by remember { mutableStateOf(0) }

    val state by viewmodel.uiState.collectAsState()
    var showAddingOptions by remember { mutableStateOf(false) }

    var showingOptionSelector by remember { mutableStateOf(true) }
    var showingDateTitle by remember { mutableStateOf(true) }

    var showingSubtaskAdding by remember { mutableStateOf(false) }
    var habitForSubTaskAdding by remember { mutableStateOf<HabitWithProgress?>(null) }

    var showingCounter by remember { mutableStateOf(false) }
    var habitForCounter by remember { mutableStateOf<HabitWithProgress?>(null) }

    var showingSkipAlert by remember { mutableStateOf(false) }
    var habitForShowingAlert by remember { mutableStateOf<HabitWithProgress?>(null) }

    var hideJob by remember { mutableStateOf<Job?>(null) }

    // Intro dialog state
    var showIntroDialog by remember {
        mutableStateOf(habitPreference?.getIntro(default = true) == true && introVideoUrl.isNotEmpty())
    }

    LaunchedEffect(state.selectedDate) {
        viewmodel.loadHomePage(state.selectedDate)
    }
    LaunchedEffect(scrollState.isScrollInProgress) {
        if(state.isShowingDatePicker && scrollState.isScrollInProgress){
            viewmodel.closeDatePicker()
        }
    }
    LaunchedEffect(scrollState.value) {
        val currentOffset = scrollState.value
        showingDateTitle = currentOffset <= 10

        if(showingDateTitle){
            showingOptionSelector = true
        }else{
            if( currentOffset > previousScrollOffset || currentOffset == scrollState.maxValue){
                showingOptionSelector = false
            }else{
                showingOptionSelector = true
            }
        }
        previousScrollOffset = currentOffset
    }
    LaunchedEffect(state.isShowingDatePicker){
        if(state.isShowingDatePicker && hideJob == null){
            scope.launch {
                delay(1700)
                if(hideJob == null) {
                    viewmodel.closeDatePicker()
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ){
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 30.dp, start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.three_dots),
                        contentDescription = "menu",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ){innerPadding->
        //adding option
        if(showAddingOptions){
            AddingOption(
                onDismiss = {
                    showAddingOptions = false
                },
                onAddHabitClicked = {
                    navController.navigate(Screen.AddHabit.createRoute(date = state.selectedDate))
                },
                onAddGoalClicked = {
                    navController.navigate(Screen.AddGoal.createRoute())
                }
            )
        }
        //session habit
        if(showingSubtaskAdding && habitForSubTaskAdding != null){
            AddSubTask(
                habitWithProgress = habitForSubTaskAdding!!,
                onUpdateSubTask = {
                    scope.launch{
                        viewmodel.addUpdateSubTasks(it,habitForSubTaskAdding!!.progress.progressId)
                        showingSubtaskAdding = false
                    }

                }
            )
        }

        //counter habit
        if(showingCounter && habitForCounter != null){
            CountUpdater(
                habitWithProgress = habitForCounter!!,
                onUpdateCounter = {
                    scope.launch{
                        viewmodel.onUpdateCounter(it,habitForCounter!!)
                        showingCounter = false
                    }

                }
            )
        }
        //for skipping alert of one time ha it
        if(showingSkipAlert && habitForShowingAlert != null){
            SkipAlertDialog(
                habitWithProgress = habitForShowingAlert!!,
                onDismiss = {
                    showingSkipAlert = false
                },
                onConfirm = {
                    viewmodel.onSkipHabit(it.progress)
                    viewmodel.loadHomePage(state.selectedDate)
                }
            )
        }

        // Intro video dialog for first-time users
        if (showIntroDialog) {
            IntroDialog(
                videoUrl = introVideoUrl,
                onDismiss = {
                    habitPreference?.updateIntro(false)
                    showIntroDialog = false
                },
                onVideoEnd = {
                    habitPreference?.updateIntro(false)
                }
            )
        }


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .padding(bottom = 80.dp)
        ){
            val date = state.selectedDate.format(
                DateTimeFormatter.ofPattern("dd MMM")
            )

            AnimatedVisibility(showingDateTitle) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ){
                    Text(
                        text = if(state.selectedDate == LocalDate.now()) "Today,$date" else "$date",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = instrumentSerif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            //date picker
                            if(state.isShowingDatePicker){
                                viewmodel.closeDatePicker()
                            }else{
                                viewmodel.openDatePicker()
                            }
                        }
                    ) {
                        val rotation by animateFloatAsState(
                            targetValue = if(state.isShowingDatePicker) 180f else 0f,
                            animationSpec = tween(300),
                            label = "datePicker"
                        )
                        Icon(
                            painter = painterResource(R.drawable.toggle),
                            contentDescription = "select date",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier =Modifier.graphicsLayer(
                                rotationZ = rotation
                            )
                        )
                    }
                }
            }
            AnimatedVisibility(showingOptionSelector) {
                OptionSelector(
                    selectedOption = state.selectedOption,
                    onOptionSelected = {viewmodel.onOptionSelected(it)}
                )
            }
            Spacer(Modifier.height(16.dp))
            //habits
            Column(modifier = Modifier.verticalScroll(
                scrollState,
                flingBehavior = ScrollableDefaults.flingBehavior()
            )
            ) {
                if(state.selectedOption == HomeScreenOption.Habits){
                    //no started / ongoing
                    state.habitWithProgressList
                        .filter { it.progress.status == Status.NotStarted }
                        .forEach{ habit->
                            key(habit.habit.habit_id){
                                HabitCard(
                                    habitWithProgress = habit,
                                    onSubTaskAdding = {
                                        showingSubtaskAdding = true
                                        habitForSubTaskAdding = it
                                    },
                                    onToggle = {
                                        viewmodel.toggleSubtask(it)
                                    },
                                    onSkip = {
                                        showingSkipAlert = true
                                        habitForShowingAlert = it
                                    },
                                    onDone = {
                                        scope.launch {
                                            viewmodel.onDoneHabit(it)
                                            viewmodel.loadHomePage(state.selectedDate)
                                        }
                                    },
                                    onAddCounter = {
                                        showingCounter = true
                                        habitForCounter = it
                                    },
                                    onStartDuration = {
                                        if(state.ongoingHabit != null && it != state.ongoingHabit ){
                                            scope.launch {
                                                Toast.makeText(context,"Already Habit Running", Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            navController.navigate(Screen.DurationScreen.createRoute(it))
                                        }
                                    },
                                    onStartSession = {
                                        if(state.ongoingHabit != null && it != state.ongoingHabit ){
                                            scope.launch {
                                                Toast.makeText(context,"Already Habit Running", Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            navController.navigate(Screen.SessionScreen.createRoute(it))
                                        }
                                    },
                                    onFutureTaskStateChange = {
                                        scope.launch {
                                            Toast.makeText(context,"Can't start Future Tasks", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onHabitClick = {
                                        navController.navigate(Screen.HabitScreen.createRoute(it))
                                    }
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    //finished or skipped //todo
                    state.habitWithProgressList
                        .filter { it.progress.status != Status.NotStarted }
                        .forEach{ habit->
                            key(habit.habit.habit_id){
                                HabitCard(
                                    habitWithProgress = habit,
                                    onSubTaskAdding = {
                                        showingSubtaskAdding = true
                                        habitForSubTaskAdding = it
                                    },
                                    onToggle = {
                                        viewmodel.toggleSubtask(it)
                                    },
                                    onUnSkip = {
                                        scope.launch {
                                            viewmodel.onUnSkipDoneHabit(it)
                                            viewmodel.loadHomePage(state.selectedDate)
                                        }
                                    },
                                    onAddCounter = {
                                        showingCounter = true
                                        habitForCounter = it
                                    },
                                    onFutureTaskStateChange = {
                                        scope.launch {
                                            Toast.makeText(context,"Can't start Future Tasks", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onHabitClick = {
                                        navController.navigate(Screen.HabitScreen.createRoute(it))
                                    }
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                    //if showing ongoing timer then padding
                    if(state.ongoingHabit != null){
                        Spacer(Modifier.height(150.dp))
                    }
                }
                else{//todos
                    Spacer(Modifier.height(16.dp))
                    state.todos
                        .forEach { todo->
                            TodoEditor(
                                todo = todo,
                                onChange = {
                                    viewmodel.updateTodo(it,todo.taskId)
                                },
                                onToggle = {
                                    viewmodel.toggleTodo(todo.taskId)
                                },
                                onDelete = {
                                    viewmodel.deleteTodo(it)
                                },
                                onAddNewSubTask = {
                                    viewmodel.addTodo()
                                }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    Text(
                        text = "+ Add Todos",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.scrim,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable{
                                viewmodel.addTodo()
                            }
                    )
                }
            }

        }

        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ){  //date picker
            Box(
                modifier =  Modifier.padding(top = 120.dp)
            ){
                AnimatedVisibility(
                    visible = state.isShowingDatePicker,
                    enter = expandVertically(),
                    exit = shrinkVertically(animationSpec = tween(100))
                ) {
                    DatePicker(
                        currentDate = state.selectedDate,
                        onChange = {
                            viewmodel.onDateSelected(it)
                            viewmodel.closeDatePicker()
                        },
                        onScrollChanged = {
                            hideJob?.cancel()
                            if(!it){
                                hideJob = scope.launch {
                                    delay(2000)
                                    viewmodel.closeDatePicker()
                                }
                            }
                        }
                    )

                }
            }
            Box(modifier.align(Alignment.BottomCenter)){
                if(state.habitWithProgressList.isEmpty() && state.selectedOption == HomeScreenOption.Habits ){
                    Icon(
                        painter = painterResource(R.drawable.empty_habit_icon),
                        contentDescription = "empty habit",
                        tint = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 90.dp)
                    )
                }
            }
            //ongoing timer
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ){
                AnimatedVisibility(
                    visible = state.ongoingHabit != null,
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    state.ongoingHabit?.let {
                        RunningTimer(
                            habitWithProgress = state.ongoingHabit!!,
                            hour = state.ongoingHour,
                            minute = state.ongoingMinute,
                            second = state.ongoingSecond,
                            onUpdateTimer = {h,m,s->
                                viewmodel.updateOngoingTimer(h,m,s)
                            },
                            onClick = {
                                if(state.ongoingHabit!!.habit.type == HabitType.Session){
                                    navController.navigate(Screen.SessionScreen.createRoute(it))
                                }else{
                                    navController.navigate(Screen.DurationScreen.createRoute(it))
                                }
                            },
                            onTimerFinished = {
                                scope.launch{
                                    viewmodel.finishTimer()
                                    viewmodel.loadHomePage(state.selectedDate)
                                }
                            },
                            modifier = Modifier
                                .padding(bottom= 90.dp)
                        )
                    }
                }
            }
            //BottomNavBar
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ){
                BottomNavBar(
                    selectedScreen = Screen.Home,
                    onAddClick = {showAddingOptions= !showAddingOptions},
                    onNavigate = {
                        if(it == Screen.Home){
                            navController.navigate(it.route){
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }else{
                            navController.navigate(it.route)
                        }
                    }
                )
            }
        }

    }

}


