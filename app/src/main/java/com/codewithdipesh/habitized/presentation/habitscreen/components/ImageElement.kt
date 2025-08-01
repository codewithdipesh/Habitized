package com.codewithdipesh.habitized.presentation.habitscreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.domain.model.ImageProgress
import com.codewithdipesh.habitized.ui.theme.regular
import java.io.File
import java.time.LocalDate
import java.util.Locale

@Composable
fun ImageElement(
    image: ImageProgress,
    onclick : (ImageProgress?)->Unit = {},
    onImageShowed : (String) ->Unit = {},
    modifier: Modifier = Modifier
){
    val file = File(image?.imagePath ?: "")

    val painter = rememberAsyncImagePainter(model = file)

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ){
        //image
        if (file.exists()) {
            Image(
                painter = painter,
                contentDescription = "Habit image",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable{
                        onImageShowed(image.imagePath)
                    }
            )
        } else {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "no image",
                    tint = MaterialTheme.colorScheme.scrim
                )
            }
        }
        //description
        Column(
            modifier = Modifier
                .weight(.65f)
                .clickable{
                   onclick(image)
                }
                .height(100.dp),
            verticalArrangement = if(image == null) Arrangement.Center else Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = "${image.date.dayOfMonth} ${image.date.month.name.take(3).lowercase().capitalize(Locale.ROOT)} ${image.date.year}",
                style = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 18.sp,
                    fontFamily = regular,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = image.description.take(87) + if(image.description.length > 87) "..." else "",
                style = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontFamily = regular,
                    fontWeight = FontWeight.Normal
                )
            )
        }

    }
}

