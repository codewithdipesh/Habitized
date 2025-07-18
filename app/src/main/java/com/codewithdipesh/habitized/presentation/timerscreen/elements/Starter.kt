package com.codewithdipesh.habitized.presentation.timerscreen.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.delay

@Composable
fun Starter(
    modifier: Modifier = Modifier,
    onFinish : ()->Unit
){
    var counter by remember {
        mutableStateOf(3)
    }

    LaunchedEffect(Unit){
        while(counter > 0){
            delay(1000)
            counter--
        }
        onFinish()
    }

    AlertDialog(
        containerColor = Color.Transparent,
        text = {
            AnimatedContent(
                targetState = counter,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }, label = "animated content"
            ){targetState->
                Text(
                    text = "$targetState",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 40.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        },
        onDismissRequest = {
            //nothing
        },
        confirmButton = {
            //nothing
        },
        dismissButton = {
           //nothing
        }
    )
}