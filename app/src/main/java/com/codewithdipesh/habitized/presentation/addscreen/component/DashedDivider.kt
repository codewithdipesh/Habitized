package com.codewithdipesh.habitized.presentation.addscreen.component

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Composable
fun DashedDivider(
    thickness: Dp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    phase: Float = 10f,
    intervals: FloatArray = floatArrayOf(10f, 10f),
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val dividerHeight = thickness.toPx()
        drawRoundRect(
            color = color,
            style = Stroke(
                width = dividerHeight,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = intervals,
                    phase = phase
                )
            )
        )
    }
}