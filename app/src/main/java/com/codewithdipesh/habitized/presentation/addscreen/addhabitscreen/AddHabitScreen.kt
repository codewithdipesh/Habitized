package com.codewithdipesh.habitized.presentation.addscreen.addhabitscreen

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.CountParam
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.presentation.addscreen.AddViewModel
import com.codewithdipesh.habitized.presentation.addscreen.component.AddScreenTopBar
import com.codewithdipesh.habitized.presentation.addscreen.component.ColorChoser
import com.codewithdipesh.habitized.presentation.addscreen.component.DashedDivider
import com.codewithdipesh.habitized.presentation.addscreen.component.GoalsForHabit
import com.codewithdipesh.habitized.presentation.addscreen.component.InputElement
import com.codewithdipesh.habitized.presentation.addscreen.component.MonthlyDaySelector
import com.codewithdipesh.habitized.presentation.addscreen.component.ParamChoser
import com.codewithdipesh.habitized.presentation.addscreen.component.ReminderTimePicker
import com.codewithdipesh.habitized.presentation.addscreen.component.Selector
import com.codewithdipesh.habitized.presentation.addscreen.component.SlidingButton
import com.codewithdipesh.habitized.presentation.addscreen.component.TimePicker
import com.codewithdipesh.habitized.presentation.addscreen.component.WeekDaySelector
import com.codewithdipesh.habitized.presentation.navigation.Screen
import com.codewithdipesh.habitized.ui.theme.playfair
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import androidx.core.net.toUri

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    modifier: Modifier = Modifier,
    id: UUID? = null,
    navController: NavController,
    viewmodel: AddViewModel,
    date: LocalDate
) {
    val state by viewmodel.habitUiState.collectAsState()
    val scrollstate = rememberScrollState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isShowingGoals by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewmodel.clearHabitUI()
        }
    }

    LaunchedEffect(Unit) {
        val keys = state.colorOptions.keys.toList()
        val color = keys.random()
        viewmodel.setColor(state.colorOptions.get(color)!!)
        viewmodel.getGoals()
        if (id != null) {
            viewmodel.init(id)
        }
    }
    //check if alarm permission is granted or not
    LaunchedEffect(state.isShowReminderTime) {
        if (state.isShowReminderTime) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
            } else {
                true
            }
            if (!permission) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
                context.startActivity(intent)
            }
        }
    }

    LaunchedEffect(viewmodel.uiEvent) {
        viewmodel.uiEvent.collect {
            scope.launch {
                if (it == "Habit Created Successfully") {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddScreenTopBar(
                isShowingLeftIcon = true,
                leftIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(top = 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 30.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        scope.launch {
                            viewmodel.addHabit(date)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Done",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                )
            }
        }
    ) { innerPadding ->

        val sheetstate = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (state.colorOptionAvailable) {
            ColorChoser(
                onColorSelected = {
                    viewmodel.setColor(it)
                },
                onDismiss = {
                    viewmodel.toggleHabitColorOption()
                },
                sheetState = sheetstate,
                selectedColor = state.colorKey,
                colors = state.colorOptions,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.isShowingParamOptions) {
            ParamChoser(
                onParamSelected = {
                    viewmodel.setParam(it)
                },
                onDismiss = {
                    viewmodel.toggleParamOption()
                },
                sheetState = sheetstate,
                selectedParam = state.countParam ?: CountParam.getParams(state.type).first(),
                params = state.paramOptions
            )
        }

        if (isShowingGoals) {
            GoalsForHabit(
                goals = state.availableGoals,
                onSelect = {
                    viewmodel.setGoal(it)
                },
                sheetState = sheetstate,
                onDismiss = {
                    isShowingGoals = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(bottom = 120.dp)
                .verticalScroll(scrollstate),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //heading
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (id == null) "Create" else "Edit",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Habit",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = playfair,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 28.sp
                    )
                )
            }
            //title
            InputElement(
                color = if (state.title.isEmpty()) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.secondary
            ) {
                Box {
                    BasicTextField(
                        value = state.title,
                        onValueChange = {
                            viewmodel.setHabitTitle(it)
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp
                        ),
                        singleLine = false,
                        maxLines = 1,
                        cursorBrush = SolidColor(colorResource(R.color.primary)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Placeholder shown only when title is empty
                    if (state.title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = TextStyle(
                                color = colorResource(R.color.light_gray), // make it look like a placeholder
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            }
            //description
            InputElement(
                color = if (state.description.isEmpty()) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.secondary
            ) {
                Box {
                    BasicTextField(
                        value = state.description,
                        onValueChange = {
                            viewmodel.setHabitDescription(it)
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp
                        ),
                        singleLine = false,
                        maxLines = 5,
                        cursorBrush = SolidColor(colorResource(R.color.primary)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Placeholder shown only when title is empty
                    if (state.description.isEmpty()) {
                        Text(
                            text = "Description",
                            style = TextStyle(
                                color = colorResource(R.color.light_gray), // make it look like a placeholder
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            }
            //choose color
            InputElement {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Text
                    Text(
                        text = "Color",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    //color chooser
                    Box(
                        modifier = modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                colorResource(state.colorOptions.entries.first { it.value == state.colorKey }.key)
                            )
                            .clickable {
                                viewmodel.toggleHabitColorOption()
                            }
                    )

                }
            }
            //choose type
            InputElement(
                title = "Type",
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Selector(
                        options = HabitType.getHabitTypes().map { it },
                        selectedOption = state.type,
                        onOptionSelected = {
                            //todo reset timer and count viewmodel.setTargetCount(0)
                            viewmodel.setType(it)
                        },
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedOptionColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //input of targets
                    //choose param
                    if (state.type != HabitType.OneTime && state.type != HabitType.Duration) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.width(40.dp)
                            ) {
                                BasicTextField(
                                    value = if (state.countTarget != null && state.countTarget != 0) state.countTarget.toString() else "",
                                    onValueChange = {
                                        if (it.isEmpty() || it.toIntOrNull() != null) {
                                            if (it.isNotEmpty()) {
                                                if (it.toInt() < 999) viewmodel.setTargetCount(it.toInt())
                                            } else {
                                                viewmodel.setTargetCount(0)
                                            }
                                        }
                                    },
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp
                                    ),
                                    singleLine = true,
                                    maxLines = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(4.dp))
                                DashedDivider(
                                    thickness = 2.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = state.countParam?.displayName ?: "choose",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            IconButton(
                                onClick = {
                                    viewmodel.toggleParamOption()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.option_up_down_icon),
                                    contentDescription = "drop down",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                        }
                    }
                    if (state.type == HabitType.Duration || state.type == HabitType.Session) {
                        if (state.type == HabitType.Session) {
                            Text(
                                text = "For Each",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontSize = 16.sp
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //hour
                            TimePicker(
                                width = 80.dp,
                                itemHeight = 24.dp,
                                numberOfDisplayItems = 3,
                                items = (0..12).toList(),
                                initialItem = state.selectedHour,
                                itemScaleFont = 1.5f,
                                fontSize = 16,
                                textFont = regular,
                                textWeight = FontWeight.Normal,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                nonSelectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.3f
                                ),
                                onItemSelected = { item ->
                                    viewmodel.setHours(item)
                                }
                            )
                            Text(
                                text = ":",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontSize = 16.sp
                                )
                            )
                            //minutes
                            TimePicker(
                                width = 80.dp,
                                itemHeight = 24.dp,
                                numberOfDisplayItems = 3,
                                items = (0..59).toList(),
                                initialItem = state.selectedMinute,
                                itemScaleFont = 1.5f,
                                fontSize = 16,
                                textFont = regular,
                                textWeight = FontWeight.Normal,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                nonSelectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.3f
                                ),
                                onItemSelected = { item ->
                                    viewmodel.setMinutes(item)
                                }
                            )
                            Text(
                                text = ":",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontSize = 16.sp
                                )
                            )
                            //seconds
                            TimePicker(
                                width = 80.dp,
                                itemHeight = 24.dp,
                                numberOfDisplayItems = 3,
                                items = (0..59).toList(),
                                initialItem = state.selectedSeconds,
                                itemScaleFont = 1.5f,
                                fontSize = 16,
                                textFont = regular,
                                textWeight = FontWeight.Normal,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                nonSelectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.3f
                                ),
                                onItemSelected = { item ->
                                    viewmodel.setSeconds(item)
                                }
                            )
                        }
                    }
                }
            }
            //link goal
            InputElement {
                //Text
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (state.goal_id == null) "+ Link Goal" else state.goal_name,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.clickable {
                            isShowingGoals = true
                        }
                    )
                }
            }
            //frequency
            InputElement(
                title = "Frequency"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Selector(
                        options = Frequency.getTypes().map { it },
                        selectedOption = state.frequency,
                        onOptionSelected = {
                            viewmodel.setFrequency(it)
                        },
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedOptionColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.frequency == Frequency.Weekly) {
                        WeekDaySelector(
                            daysMap = state.days_of_week,
                            onSelect = {
                                viewmodel.onSelectWeekday(it)
                            }
                        )
                    }
                    if (state.frequency == Frequency.Monthly) {
                        MonthlyDaySelector(
                            selectedDays = state.daysOfMonth,
                            onDaySelected = {
                                viewmodel.onSelectDayofMonth(it)
                            }
                        )
                    }
                }
            }
            //Reminder
            InputElement {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reminder",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        SlidingButton(
                            isSelected = state.isShowReminderTime,
                            onToggle = {
                                viewmodel.toggleReminderOption()
                            }
                        )
                    }
                    //timer
                    ReminderTimePicker(
                        reminderTime = state.reminder_time,
                        onSelect = {
                            viewmodel.setReminderTime(it)
                        },
                        isReminderEnabled = state.isShowReminderTime
                    )

                }
            }

        }
    }

}


