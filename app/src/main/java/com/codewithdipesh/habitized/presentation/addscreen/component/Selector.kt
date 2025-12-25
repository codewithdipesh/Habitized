package com.codewithdipesh.habitized.presentation.addscreen.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.ui.theme.instrumentSerif

@Composable
fun <T> Selector(
    modifier: Modifier = Modifier,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    backgroundColor: Color,
    selectedTextColor: Color,
    selectedOptionColor: Color,
    height: Int = 44,
    showBadge : Boolean = false,
    badgeColor : Color = MaterialTheme.colorScheme.primary,
    badgeText : String = "",
    badgePosition : Alignment = Alignment.TopEnd,
    shape: Shape = RoundedCornerShape(15.dp),
    enabled: Boolean = true
) {
    // Remember the width of the Selector container
    var containerWidth by remember { mutableStateOf(0) }

    // Calculate the width of each option dynamically
    val optionWidth = if (options.isNotEmpty() && containerWidth > 0) {
        containerWidth / options.size
    } else 0

    // Find the index of the selected option
    val selectedIndex = options.indexOf(selectedOption).coerceAtLeast(0)

    // Animate the offset (in pixels) of the sliding background
    val animatedOffset by animateDpAsState(
        targetValue = with(LocalDensity.current) { (optionWidth * selectedIndex).toDp() },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .height(height.dp)
            .clip(shape)
            .background(backgroundColor)
            .onSizeChanged { containerWidth = it.width },
        contentAlignment = Alignment.CenterStart
    ) {
        // Sliding background
        if (optionWidth > 0) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(with(LocalDensity.current) { optionWidth.toDp() })
                    .fillMaxHeight()
                    .padding((height/10).dp)
                    .clip(shape)
                    .background(selectedOptionColor.copy(0.6f))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = enabled
                        ) {
                            onOptionSelected(option)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.toString(),
                        style = TextStyle(
                            color = if (option == selectedOption) {
                                if (enabled) selectedTextColor else selectedTextColor.copy(0.5f)
                            } else {
                                if (enabled) selectedTextColor.copy(0.8f) else selectedTextColor.copy(0.4f)
                            },
                            fontFamily = instrumentSerif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
        if(showBadge){
            Box(
                modifier = Modifier
                    .align(badgePosition)
                    .padding(end = 10.dp,top = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(0.6f))
            ){
                Text(
                    text = badgeText,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = instrumentSerif,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 8.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp,vertical=2.dp)
                )
            }
        }
    }
}
