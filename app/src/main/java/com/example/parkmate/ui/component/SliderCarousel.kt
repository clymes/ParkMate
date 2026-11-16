package com.example.parkmate.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun <T> SliderCarousel(
    items: List<T>,
    itemQuantity: Int,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true,
    onAddClick: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    val shouldShowAddButton = showAddButton && items.size < itemQuantity

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                content(item)
            }
        }

        if (shouldShowAddButton) {
            item {
                AddButton(onClick = onAddClick)
            }
        }
    }
}

@Composable
private fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokeColor = MaterialTheme.colorScheme.outline
    
    Box(
        modifier = modifier
            .size(80.dp)
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
                drawRoundRect(
                    color = strokeColor,
                    style = stroke,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Aggiungi",
            tint = strokeColor
        )
    }
}
