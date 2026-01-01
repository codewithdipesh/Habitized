package com.codewithdipesh.habitized.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular

@Composable
fun FirstScreen(
    onContinue : () -> Unit = {},
    onNavigateBack : () -> Unit = {}
) {

    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(Color(0XFF226649))
    ){
        //leaf and grass
        Image(
            painter = painterResource(id = R.drawable.green_fat_leaf),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart)
                .padding(top = 50.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.green_slim_leaf),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd)
                .padding(start = 80.dp)
                .offset(y= 150
                    .dp)
        )



        //main image
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .offset(y= (-100).dp)
                .clip(RoundedCornerShape(20.dp))
        ){
            Image(
                painter = painterResource(id = R.drawable.onboarding1_pic),
                contentDescription = "Onboarding Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        //goal
        Image(
            painter = painterResource(id = R.drawable.goal),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd)
                .offset(y= 150.dp,x= (-18).dp)
        )

        //habits
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)),
            modifier = Modifier.align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = (-18).dp )
        ) {
            Image(
                painter = painterResource(id = R.drawable.habits),
                contentDescription = null,
            )
        }


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
                text = "Make Habits",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 44.sp
                )
            )
            Text(
                text = "According To Your Goals",
                style = TextStyle(
                    color = Color.White,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Small Steps at a Time Toward Goal",
                style = TextStyle(
                    color = Color.White,
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
                .background(Color.White)
                .clickable{
                    onContinue()
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Continue",
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(16.dp)
            )
        }

    }


}

@Preview
@Composable
fun FirstScreenPreview() {
    FirstScreen()
}