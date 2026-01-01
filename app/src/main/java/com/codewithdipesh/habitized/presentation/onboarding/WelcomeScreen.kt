package com.codewithdipesh.habitized.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular
import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D1509))
    ){
        //Image
        Image(
            painter = painterResource(id = R.drawable.welcome_screen_hbitized_logo),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd)
                .offset(y= -(50).dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp)
                .offset(y = 150.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Build",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp
                )
            )
            Row {
                Text(
                    text = "Better",
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = regular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 38.sp
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Habits",
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp
                    )
                )
            }
            Text(
                text = "Stay",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp
                )
            )
            Text(
                text = "Consistent",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 38.sp
                )
            )
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ){
                //border
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(55.dp)
                )
                //circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}