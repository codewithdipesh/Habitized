package com.codewithdipesh.habitized.presentation.homescreen.component

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    onDelete : (UUID) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                    .background(
                        if (todo.isCompleted) onSurface
                        else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color =
                            if (todo.isCompleted) onSurface
                            else textColor
                    )
                    .clickable {
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check",
                        tint = textColor
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            //title
            Box(){
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
                    maxLines = 3,
                    cursorBrush = SolidColor(colorResource(R.color.primary)),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).onFocusChanged{
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    }
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()  // This will request focus when the Composable is first displayed
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