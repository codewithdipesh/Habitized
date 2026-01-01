package com.codewithdipesh.habitized.presentation.onboarding

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.delay

@Composable
fun ThirdScreen(
    onContinue : () -> Unit = {},
    onNavigateBack : () -> Unit = {}
) {

    var startPhoneAnimation by rememberSaveable { mutableStateOf(false) }
    var startImageLogAnimation by rememberSaveable { mutableStateOf(false) }

    val phoneYOffset by animateIntAsState(
        targetValue = if (startPhoneAnimation) -200 else 0,
        animationSpec = tween(durationMillis = 700)
    )

    val imageLogXOffset by animateIntAsState(
        targetValue = if (startImageLogAnimation) -90 else 0,
        animationSpec = tween(durationMillis = 700)
    )
    val imageLogYOffset by animateIntAsState(
        targetValue = if (startImageLogAnimation) -200 else 0,
        animationSpec = tween(durationMillis = 700)
    )

    LaunchedEffect(Unit) {
        startPhoneAnimation = true
        delay(300)
        startImageLogAnimation = true
    }

   Box(modifier = Modifier.fillMaxSize()
        .background(Color(0xFFECA856))
   ){
        //background
       Image(
           painter = painterResource(id = R.drawable.bg_grid),
           contentDescription = null,
           modifier = Modifier
               .align(Alignment.TopCenter)
               .fillMaxWidth()
               .offset(y=60.dp)
       )
        //image logging
        if(startImageLogAnimation) {
            Image(
                painter = painterResource(id = R.drawable.image_loging),
                contentDescription = "Onboarding Image",
                modifier = Modifier.align(Alignment.Center)
                    .width(180.dp)
                    .offset(x= imageLogXOffset.dp , y=imageLogYOffset.dp)
            )
        }
        //main image
        Image(
            painter = painterResource(id = R.drawable.phone_photo),
            contentDescription = "Onboarding Image",
            modifier = Modifier.align(Alignment.BottomCenter)
                .width(220.dp)
                .offset(y=phoneYOffset.dp)
        )

        //Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .clip(RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp))
                .background(Color.White)
        )

        //Title and heading
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.Start,
        ){
            Text(
                text = "Dopamine Boost",
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 44.sp
                )
            )
            Text(
                text = "Discipline Over Motivation",
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Streaks , Stats and Image Logging help you stay stick to your habit",
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )
            )
        }

        //button

        //IconButton
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 30.dp, start = 16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.2f))
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        //Continue Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.Black)
                .clickable{
                    onContinue()
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Continue",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(16.dp)
            )
        }

    }


}

