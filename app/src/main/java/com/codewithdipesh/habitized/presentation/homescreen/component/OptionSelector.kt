package com.codewithdipesh.habitized.presentation.homescreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.presentation.homescreen.HomeScreenOption
import com.codewithdipesh.habitized.ui.theme.instrumentSerif

@Composable
fun OptionSelector(
    selectedOption: HomeScreenOption,
    onOptionSelected: (HomeScreenOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        //Habits
        Box(
            modifier = Modifier
                .width(screenWidth/2)
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(color =
                    if (selectedOption == HomeScreenOption.Habits) MaterialTheme.colorScheme.onTertiary
                    else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(50.dp)
                )
                .clickable{
                    onOptionSelected(HomeScreenOption.Habits)
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Habits",
                style = TextStyle(
                    color = if (selectedOption == HomeScreenOption.Habits) MaterialTheme.colorScheme.inverseOnSurface
                            else MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            )
        }
        Spacer(Modifier.width(16.dp))

        //Todos
        Box(
            modifier = Modifier
                .width(screenWidth/2)
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(color =
                    if (selectedOption == HomeScreenOption.Todos) MaterialTheme.colorScheme.onTertiary
                    else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(50.dp)
                )
                .clickable{
                    onOptionSelected(HomeScreenOption.Todos)
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Todos",
                style = TextStyle(
                    color = if (selectedOption == HomeScreenOption.Todos) MaterialTheme.colorScheme.inverseOnSurface
                    else MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            )
        }

    }


}