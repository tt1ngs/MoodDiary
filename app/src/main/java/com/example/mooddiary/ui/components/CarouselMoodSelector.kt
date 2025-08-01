package com.example.mooddiary.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselMoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = Mood.entries
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val itemWidth = 80.dp
    val itemWidthPx = with(density) { itemWidth.toPx() }

    var centerIndex by remember { mutableIntStateOf(3) } // Начинаем с нейтрального

    // Инициализация - устанавливаем центральный элемент
    LaunchedEffect(Unit) {
        listState.scrollToItem(centerIndex)
    }

    // Отслеживание прокрутки для определения центрального элемента
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            val centerX = listState.layoutInfo.viewportSize.width / 2f
            val centerItem = listState.layoutInfo.visibleItemsInfo.minByOrNull { itemInfo ->
                abs((itemInfo.offset + itemInfo.size / 2f) - centerX)
            }

            centerItem?.let { item ->
                if (item.index != centerIndex) {
                    centerIndex = item.index.coerceIn(0, moods.size - 1)
                    val newMood = moods[centerIndex]
                    if (newMood != selectedMood) {
                        onMoodSelected(newMood)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 160.dp),
            flingBehavior = rememberSnapFlingBehavior(listState),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(moods.size) { index ->
                val mood = moods[index]
                val isCenter = index == centerIndex
                val distance = abs(index - centerIndex)

                CarouselMoodItem(
                    mood = mood,
                    isSelected = isCenter,
                    distance = distance,
                    onClick = {
                        centerIndex = index
                        scope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )
            }
        }

        // Центральный индикатор
        Box(
            modifier = Modifier
                .size(4.dp)
                .graphicsLayer { alpha = 0.3f },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .graphicsLayer {
                        scaleX = 1f
                        scaleY = 1f
                    }
            )
        }
    }
}

@Composable
fun CarouselMoodItem(
    mood: Mood,
    isSelected: Boolean,
    distance: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.0f
            distance == 1 -> 0.7f
            else -> 0.5f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = when {
            isSelected -> 1f
            distance == 1 -> 0.6f
            else -> 0.3f
        },
        animationSpec = tween(300),
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.size(if (isSelected) 72.dp else 56.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = getMoodColor(mood).copy(alpha = if (isSelected) 0.9f else 0.6f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 2.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Показываем только точку для неактивных элементов, эмодзи для активного
                if (isSelected) {
                    Text(
                        text = mood.emoji,
                        fontSize = 28.sp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer {
                                clip = true
                                shape = CircleShape
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                        )
                    }
                }
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(mood.labelRes),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

private fun getMoodColor(mood: Mood): Color {
    return when (mood) {
        Mood.VERY_HAPPY -> MoodVeryHappy
        Mood.HAPPY -> MoodHappy
        Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
        Mood.NEUTRAL -> MoodNeutral
        Mood.SLIGHTLY_SAD -> MoodSlightlySad
        Mood.SAD -> MoodSad
        Mood.VERY_SAD -> MoodVerySad
    }
}
