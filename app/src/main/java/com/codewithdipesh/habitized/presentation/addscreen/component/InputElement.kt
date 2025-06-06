package com.codewithdipesh.habitized.presentation.addscreen.component

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.ui.theme.regular

@Composable
fun InputElement(
    modifier: Modifier = Modifier,
    title : String? = null,
    color : androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    element : @Composable () -> Unit,
) {


    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column{
           if(title != null){
               Text(
                   text = title,
                   style = TextStyle(
                       color = MaterialTheme.colorScheme.onPrimary,
                       fontFamily = regular,
                       fontSize = 16.sp,
                       fontWeight = FontWeight.Bold
                   )
               )
           }
           Spacer(Modifier.height(8.dp))
           element()
        }
    }


}