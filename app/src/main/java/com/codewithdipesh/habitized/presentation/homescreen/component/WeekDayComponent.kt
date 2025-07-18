package com.codewithdipesh.habitized.presentation.homescreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.codewithdipesh.habitized.ui.theme.ndot
import com.codewithdipesh.habitized.ui.theme.playfair
import com.codewithdipesh.habitized.ui.theme.regular
import com.kizitonwose.calendar.core.WeekDay
import java.time.LocalDate
import java.util.Locale

@Composable
fun WeekDayComponent(
    day: WeekDay,
    selected: Boolean = false,
    onClick: (LocalDate) -> Unit = {},
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Box(
        modifier = Modifier
            .width(screenWidth / 7)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
            .clickable { onClick(day.date) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault()),
                style = TextStyle(
                    color = if(selected) MaterialTheme.colorScheme.outlineVariant
                            else MaterialTheme.colorScheme.onPrimary,
                    fontFamily = regular,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            )
            Text(
                text = day.date.dayOfMonth.toString(),
                style = TextStyle(
                    color = if(selected) MaterialTheme.colorScheme.outlineVariant
                    else MaterialTheme.colorScheme.onPrimary,
                    fontFamily = playfair,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Text(
                text = day.date.month.getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault()),
            )
        }
    }
}
