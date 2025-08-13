package com.codewithdipesh.habitized.presentation.habitscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.Frequency
import com.codewithdipesh.habitized.domain.model.HabitType
import com.codewithdipesh.habitized.domain.model.ImageProgress
import com.codewithdipesh.habitized.presentation.habitscreen.components.AddEditImageProgress
import com.codewithdipesh.habitized.presentation.habitscreen.components.CalendarStat
import com.codewithdipesh.habitized.presentation.habitscreen.components.Element
import com.codewithdipesh.habitized.presentation.habitscreen.components.ImageElement
import com.codewithdipesh.habitized.presentation.habitscreen.components.ShowImage
import com.codewithdipesh.habitized.presentation.navigation.Screen
import com.codewithdipesh.habitized.presentation.progress.components.FireAnimation
import com.codewithdipesh.habitized.presentation.util.IntToWeekDayMap
import com.codewithdipesh.habitized.presentation.util.getOriginalColorFromKey
import com.codewithdipesh.habitized.presentation.util.toWord
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@Composable
fun HabitDetails(
    id : UUID,
    title : String,
    colorKey : String,
    modifier: Modifier = Modifier,
    viewmodel: HabitViewModel,
    navController:NavController
){
    val state by viewmodel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showImageProgress by remember { mutableStateOf(false) }
    var imageProgress by remember { mutableStateOf<ImageProgress?>(null) }

    var showFullImage by remember { mutableStateOf(false) }
    var fullImage by remember { mutableStateOf<String?>(null) }

    var showDeleteHabitBox by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            viewmodel.init(id)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewmodel.clearUi()
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                //left icon
                IconButton(
                    onClick = {navController.navigateUp()},
                    modifier = Modifier
                        .padding(top = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                //title
                Text(
                    text = "Habit",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = instrumentSerif,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(top = 40.dp)
                )
                //options(share,edit,delete
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(
                        onClick = {                                             //though date will not use there as id is present
                            navController.navigate(Screen.AddHabit.createRoute(date = LocalDate.now(),id = state.id.toString()))
                        },
                        modifier = Modifier
                            .padding(top = 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(
                        onClick = {
                            showDeleteHabitBox = true
                        },
                        modifier = Modifier
                            .padding(top = 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete",
                            tint = colorResource(R.color.delete_red)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if(showImageProgress){
            AddEditImageProgress(
                imageProgress = imageProgress,
                title = state.title,
                color = getOriginalColorFromKey(colorKey),
                onCancel = {
                    showImageProgress = false
                },
                onSave = {id,image,date,description ->
                    scope.launch {
                        viewmodel.saveImage(id,image,date,description)
                        viewmodel.init(state.id!!)
                        showImageProgress = false
                    }
                },
                onDelete = {
                    scope.launch {
                        viewmodel.deleteImage(it.id)
                        viewmodel.init(id)
                    }
                }
            )
        }
        if(showDeleteHabitBox){
            DeleteHabitBox(
                onConfirm = {
                    scope.launch {
                        viewmodel.deleteHabit(id)
                        navController.navigateUp()
                    }
                },
                onCancel = {
                    showDeleteHabitBox = false
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            //title , frequency , and target
            Element(){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    //title, frequency
                    Column (
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Text(
                            text = title,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Text(
                            text = when(state.frequency){
                                Frequency.Daily -> "Everyday"
                                Frequency.Weekly -> {
                                    IntToWeekDayMap(state.days_of_week)
                                        .filter { it.value == true }
                                        .keys
                                        .joinToString(", ") { it.name.lowercase().take(3) }
                                }
                                Frequency.Monthly -> {
                                    state.daysOfMonth!!.joinToString(", ")
                                }
                                else -> "Everyday"
                            },
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp
                            )
                        )
                    }
                    //session , target
                    Column (
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        when(state.type){
                            HabitType.OneTime -> {
                                Text(
                                    text = "OneTime",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            HabitType.Duration -> {
                                Text(
                                    text = state.targetTime?.toWord() ?: "",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            else -> {
                                Text(
                                    text = "${state.targetCount} ${state.countParam?.displayName}",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                        //only for session
                        if(state.type == HabitType.Session){
                            Text(
                                text = state.targetTime?.toWord() ?: "",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontFamily = regular,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
            //streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                //current streak
                Element(
                    modifier = Modifier.weight(1f)
                ){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text = "${state.currentStreak} days",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = instrumentSerif,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 20.sp
                            )
                        )
                        //fire animation
                        FireAnimation(
                            modifier = Modifier.padding(start = 4.dp),
                            colorKey = colorKey,
                            loop = true,
                            size = 30
                        )
                    }
                    Text(
                        text = "Current streak",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                }
                //maximum streak
                Element(
                    modifier = Modifier.weight(1f)
                ){
                    Text(
                        text = "${state.maximumStreak} days",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Maximum streak",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                }
            }
            //completion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                //total completed
                Element(modifier = Modifier.weight(1f)){
                    Text(
                        text = "${state.totalCompleted}",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Total Completed",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                }
                //completion rate
                Element(modifier = Modifier.weight(1f)){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text = "${state.completionRate}",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = regular,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "%",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                fontFamily = regular,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Completion rate",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                }
            }
            //Description
            if(state.description != ""){
                Element(){
                    Text(
                        text = "Description",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${state.description}",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }
            }

             CalendarStat(
                 color = getOriginalColorFromKey(colorKey),
                 progressList = state.progressList
             )
            //images
            Element {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Visual Journey",
                        style = androidx.compose.ui.text.TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontFamily = regular,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    //add progress
                    IconButton(onClick = {
                        imageProgress = null
                        showImageProgress = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add progress",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                if(state.imageProgresses.isEmpty()){
                    Text(
                        text = "\uD83D\uDDBC\uFE0F Add your today's progress",
                        style = androidx.compose.ui.text.TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 20.sp,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    )
                }
                state.imageProgresses.forEach {
                    ImageElement(
                        image = it,
                        onclick = {
                            imageProgress = it
                            showImageProgress = true
                        },
                        onImageShowed = {
                            fullImage = it
                            showFullImage = true
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

        }
        //show full image
        if(showFullImage){
            ShowImage(
                imagePath = fullImage!!,
                onDismiss = {
                    showFullImage = false
                }
            )
        }
    }

}

//deleteAlertBox
@Composable
fun DeleteHabitBox(
    modifier: Modifier = Modifier,
    onConfirm : ()-> Unit,
    onCancel : () ->Unit
){
    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(
                    text = "Cancel",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onCancel()
                }
            ) {
                Text(
                    text = "Yes,Delete",
                    style = TextStyle(
                        color = colorResource(R.color.delete_red),
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                )
            }
        },
        title = {
            Text(
                text = "Delete the Habit",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        text = {
            Text(
                text = "This will delete all the progress and images associated with this habit also ",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )
        }
    )
}
