package com.pizza.psx.views.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizza.psx.presentation.helpers.findSegmentAtAngle
import com.pizza.psx.presentation.helpers.generateColorFromSymbol
import com.pizza.psx.presentation.helpers.getRandomColor
import kotlin.math.atan2


data class ChartData(
    val value: Float,
    val color: Color,
    val label: String = "",
    val price: Float
)

@Composable
fun InteractiveDonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    radius: Dp = 120.dp,
    strokeWidth: Dp = 40.dp,
    gapAngle: Float = 5f,
    onSegmentClick: (ChartData) -> Unit = {}
) {
    var selectedSegment by remember { mutableStateOf<Int?>(null) }
    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(radius * 2)
            .pointerInput(Unit) {
//                detectTapGestures { offset ->
//                    touchPosition = Offset(offset.x.toFloat(), offset.y.toFloat())
//                    val center = size.center
//                    val touchAngle = calculateAngle(center, offset)
//                    selectedSegment = findSegmentAtAngle(data, touchAngle, gapAngle)
//
//                    selectedSegment?.let { index ->
//                        onSegmentClick(data[index])
//                    }
//                }
            }
    ) {//getRandomColor
        Canvas(modifier = Modifier.matchParentSize()) {
            val total = data.sumOf { it.value.toDouble() }.toFloat()
            var startAngle = -90f
            val center = size.center
            val chartRadius = (size.minDimension - strokeWidth.toPx()) / 2

            data.forEachIndexed { index, item ->
                val sweepAngle = (item.value / total) * 360f - gapAngle
                val isSelected = selectedSegment == index
                val segmentColor = if (isSelected) item.color.copy(alpha = 0.8f) else item.color

                drawArc(
                    color = segmentColor,
                    startAngle = startAngle + gapAngle / 2,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = if (isSelected) strokeWidth.toPx() * 1.1f else strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    ),
                    size = Size(size.width - strokeWidth.toPx(), size.height - strokeWidth.toPx()),
                    topLeft = Offset(strokeWidth.toPx() / 2, strokeWidth.toPx() / 2)
                )

                startAngle += sweepAngle + gapAngle
            }

            // Draw center text
            drawIntoCanvas { canvas ->
                val textPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 32f
                    color = android.graphics.Color.WHITE
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                canvas.nativeCanvas.drawText(
                    "₹${String.format("%.1f", total)}",
                    center.x,
                    center.y + 10,
                    textPaint
                )
            }
        }

        // Tooltip
        selectedSegment?.let { index ->
            val segment = data[index]
            Tooltip(
                position = touchPosition,
                text = "${segment.label}\n₹${segment.value}",
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

private fun calculateAngle(center: Offset, touch: Offset): Float {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (angle < 0) angle += 360f
    return angle
}

@Composable
fun Tooltip(position: Offset, text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .offset(x = position.x.dp, y = position.y.dp - 50.dp)
            .background(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}