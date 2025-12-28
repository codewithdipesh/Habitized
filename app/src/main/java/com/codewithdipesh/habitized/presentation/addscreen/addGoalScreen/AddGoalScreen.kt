package com.codewithdipesh.habitized.presentation.addscreen.addGoalScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.presentation.addscreen.AddViewModel
import com.codewithdipesh.habitized.presentation.addscreen.component.AddScreenTopBar
import com.codewithdipesh.habitized.presentation.addscreen.component.HabitsForGoal
import com.codewithdipesh.habitized.presentation.addscreen.component.InputElement
import com.codewithdipesh.habitized.presentation.addscreen.component.SlidingButton
import com.codewithdipesh.habitized.presentation.addscreen.component.TargetDatePicker
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AddGoalScreen(
    modifier: Modifier = Modifier,
    id : UUID? = null,
    navController: NavController,
    viewmodel: AddViewModel
) {
    val state by viewmodel.goalUiState.collectAsState()
    val scrollstate = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isShowingExistingHabits by remember { mutableStateOf(false) }
    var isShowingLinkedHabits by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewmodel.clearGoalUI()
        }
    }
    LaunchedEffect(viewmodel.uiEvent) {
        viewmodel.uiEvent.collect {
            scope.launch {
                if(it == "Goal Created Successfully"){
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }else{
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if(id != null){
            viewmodel.initGoal(id)
        }
        viewmodel.getExistingHabits(id)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddScreenTopBar(
                isShowingLeftIcon = true,
                leftIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                            viewmodel.clearGoalUI()
                            },
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
        }
    ){innerPadding->

        if(isShowingExistingHabits){
            HabitsForGoal(
                clickable = true,
                habits = state.availableHabits,
                selected = state.habits,
                onDismiss = {
                    isShowingExistingHabits = false
                },
                onSelect = {
                    viewmodel.setHabitsAttachedWithGoal(it)
                },
                sheetState = sheetState
            )
        }

        if(isShowingLinkedHabits){
            HabitsForGoal(
                clickable = false,
                habits = state.habits,
                onDismiss = {
                    isShowingLinkedHabits = false
                },
                sheetState = sheetState
            )
        }

        Box(modifier = Modifier.fillMaxSize()){
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 120.dp)
                    .align(Alignment.Center)
                    .verticalScroll(scrollstate),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                //heading
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "Create",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Goal",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = instrumentSerif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 28.sp
                        )
                    )
                }
                //title
                InputElement(
                    color = if(state.title.isEmpty()) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.secondary
                ){
                    Box {
                        BasicTextField(
                            value = state.title,
                            onValueChange = {
                                viewmodel.setGoalTitle(it)
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
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
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
                //description
                InputElement(
                    color = if(state.description.isEmpty()) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.secondary
                ){
                    Box {
                        BasicTextField(
                            value = state.description,
                            onValueChange = {
                                viewmodel.setGoalDescription(it)
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                            singleLine = false,
                            maxLines = 5,
                            cursorBrush = SolidColor(colorResource(R.color.primary)),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Placeholder shown only when title is empty
                        if (state.description.isEmpty()) {
                            Text(
                                text = "Describe your Goal",
                                style = TextStyle(
                                    color = colorResource(R.color.light_gray), // make it look like a placeholder
                                    fontFamily = regular,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
                //Target Date
                InputElement{
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target Date",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            Switch(
                                checked = state.isTargetDateVisible,
                                onCheckedChange = {  viewmodel.toggleGoalTargetDateOption() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(0.3f)
                                )
                            )
                        }
                        //date
                        AnimatedVisibility(
                            visible = state.isTargetDateVisible
                        ) {
                            TargetDatePicker(
                                date = state.target_date,
                                onSelect = {
                                    viewmodel.setTargetDate(it)
                                }
                            )
                        }

                    }
                }
                //link habits
                InputElement {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxLines = 3,
                        overflow = FlowRowOverflow.Clip,
                    ) {
                        state.habits.take(6).forEach { habit ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = habit.title,
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        if (state.habits.size > 6) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        isShowingLinkedHabits = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${state.habits.size - 6}",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                    if(state.habits.isNotEmpty()){
                        Spacer(Modifier.height((8.dp)))
                    }
                    Text(
                        text = "+ link existing habits",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.clickable{
                            isShowingExistingHabits = true
                        }
                    )
                }

            }
            //button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.TopCenter
            ){
                //add button
                Column(
                    modifier = Modifier.wrapContentSize()
                        .padding(vertical = 16.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 24.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .clickable{
                                scope.launch {
                                    viewmodel.addGoal()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Done",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }

}


