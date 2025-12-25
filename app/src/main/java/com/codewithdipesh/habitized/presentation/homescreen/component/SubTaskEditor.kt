package com.codewithdipesh.habitized.presentation.homescreen.component

import android.media.MediaPlayer
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.OneTimeTask
import com.codewithdipesh.habitized.domain.model.SubTask
import com.codewithdipesh.habitized.ui.theme.regular
import java.util.UUID

@Composable
fun SubTaskEditor(
    modifier: Modifier = Modifier,
    subTask: SubTask,
    enabled : Boolean = true,
    textSize :Int = 18,
    textColor : Color =MaterialTheme.colorScheme.onPrimary,
    onSurface: Color = MaterialTheme.colorScheme.onSurface,
    onChange : (String) ->Unit = {},
    onToggleSubtask : () -> Unit= {},
    onDelete : (UUID) -> Unit = {}
) {

    Row(
        modifier =  modifier.fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = if(enabled) Arrangement.SpaceBetween else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        //checkbox and subtask title
        Row(
            modifier = Modifier
                .then(
                    if(enabled) Modifier.wrapContentSize().weight(0.7f)
                    else Modifier.fillMaxWidth()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Box(
                modifier = Modifier.size(18.dp,18.dp)
                    .background(
                        if(subTask.isCompleted) onSurface
                        else Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color =
                            if(subTask.isCompleted) onSurface
                            else textColor
                    )
                    .clickable{
                        onToggleSubtask()
                    },
                contentAlignment = Alignment.Center
            ){
                if(subTask.isCompleted){
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check",
                        tint = textColor
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            //title
            if(enabled){
                Box {
                    BasicTextField(
                        value = subTask.title,
                        onValueChange = {
                            onChange(it)
                        },
                        enabled = enabled,
                        textStyle = TextStyle(
                            color = textColor,
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = textSize.sp
                        ),
                        singleLine = false,
                        maxLines = 1,
                        cursorBrush = SolidColor(colorResource(R.color.primary)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Placeholder shown only when title is empty
                    if (subTask.title.isEmpty()) {
                        Text(
                            text = "Write Down",
                            style = TextStyle(
                                color = onSurface,
                                fontFamily = regular,
                                fontWeight = FontWeight.Normal,
                                fontSize = textSize.sp
                            )
                        )
                    }
                }
            }else{
                Text(
                    text = subTask.title,
                    style = TextStyle(
                        color = textColor,
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = textSize.sp
                    )
                )
            }


        }
        //clear icon
        if(enabled){
            IconButton(
                onClick = {
                    onDelete(subTask.subtaskId)
                },
                modifier = Modifier.weight(0.3f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Cross",
                    tint = textColor
                )
            }
        }
    }

}

@Composable
fun TodoEditor(
    modifier: Modifier = Modifier,
    todo: OneTimeTask,
    textSize :Int = 20,
    textColor : Color =MaterialTheme.colorScheme.onPrimary,
    onSurface: Color = MaterialTheme.colorScheme.primary,
    onChange : (String) ->Unit = {},
    onToggle : () -> Unit= {},
    onDelete : (UUID) -> Unit = {},
    onAddNewSubTask : () -> Unit = {},
    shouldRequestFocus: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    //scratch sound in completion - properly managed with remember and DisposableEffect
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.scratch_sound)
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    val strikeProgress by animateFloatAsState(
        targetValue = if(todo.isCompleted && todo.title.isNotEmpty() ) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "strikeThroughAnimation"
    )
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val checkBoxProgress by animateDpAsState(
        targetValue = if(todo.isCompleted) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "checkBoxAnimation"
    )

    Row(
        modifier =  modifier.fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ){
        //checkbox and subtask title
        Row(
            modifier = Modifier.wrapContentSize().weight(0.85f),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.size(26.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        if (todo.isCompleted) onSurface
                        else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color =
                            if (todo.isCompleted) onSurface
                            else textColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        //play a scratch sound
                        if(!todo.isCompleted && todo.title.isNotEmpty()){
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                        }
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check",
                        tint = textColor,
                        modifier = Modifier.size(checkBoxProgress)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            //title
            Box(
                modifier = Modifier.fillMaxWidth()
            ){
                BasicTextField(
                    value = todo.title,
                    onValueChange = {
                        onChange(it)
                    },
                    enabled = true,
                    textStyle = TextStyle(
                        color = if(todo.isCompleted) textColor.copy(0.7f) else textColor,
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = textSize.sp
                    ),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onAddNewSubTask()
                        }
                    ),
                    onTextLayout = { result ->
                        textLayoutResult = result
                    },
                    maxLines = 3,
                    cursorBrush = SolidColor(colorResource(R.color.primary)),
                    modifier = Modifier
                        .wrapContentWidth()
                        .focusRequester(focusRequester).onFocusChanged{
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    }
                )
                // Only request focus for newly added todos
                if (shouldRequestFocus) {
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
                // Placeholder shown only when title is empty
                if (todo.title.isEmpty()) {
                    Text(
                        text = "Write Down",
                        style = TextStyle(
                            color = textColor.copy(0.3f),
                            fontFamily = regular,
                            fontWeight = FontWeight.Normal,
                            fontSize = textSize.sp
                        )
                    )
                }

                //strike animation in canvas
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val layout = textLayoutResult ?: return@Canvas
                    val lineCount = layout.lineCount

                    for (line in 0 until lineCount) {
                        val start = layout.getLineLeft(line)
                        val end = layout.getLineRight(line)

                        val progress = strikeProgress.coerceIn(0f,1f)
                        val animatedEnd = start + (end - start) * progress

                        val top = layout.getLineTop(line)
                        val bottom = layout.getLineBottom(line)
                        val lineHeight = bottom - top
                        val y = top + lineHeight/ 2f

                        drawLine(
                            color = textColor.copy(0.7f),
                            start = Offset(start,y),
                            end = Offset( animatedEnd,y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }


        }
        //delete icon
        IconButton(
            onClick = {
                onDelete(todo.taskId)
            },
            modifier = Modifier.weight(0.15f)
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Cross",
                tint = textColor
            )
        }
    }

}