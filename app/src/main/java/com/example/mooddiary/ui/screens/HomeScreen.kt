package com.example.mooddiary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.ui.components.*
import com.example.mooddiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSaveMoodEntry: (Mood, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var noteText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Получаем ViewModel для доступа к uiState
    val viewModel: com.example.mooddiary.ui.viewmodel.MoodDiaryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Фоновый эффект
        AnimatedBackground(
            selectedMood = selectedMood,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Заголовок
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Карусельный селектор настроения
            CarouselMoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it },
                modifier = Modifier.height(160.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Стеклянная карточка для заметки
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.note_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.note_placeholder),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentYellow,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentYellow
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Пульсирующая кнопка сохранения
            PulsingButton(
                onClick = {
                    selectedMood?.let { mood ->
                        onSaveMoodEntry(mood, noteText)
                        selectedMood = null
                        noteText = ""
                    }
                },
                enabled = selectedMood != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_entry),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // Изменяю с черного на белый для лучшей читаемости
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Показать выбранное настроение
            selectedMood?.let { mood ->
                MoodPreview(mood = mood)
            }

            // AI рекомендации
            uiState.aiRecommendation?.let { recommendation ->
                Spacer(modifier = Modifier.height(16.dp))
                AIRecommendationCard(
                    recommendation = recommendation,
                    onDismiss = { viewModel.dismissAIRecommendation() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MoodPreview(
    mood: Mood,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                imageVector = ImageVector.vectorResource(getMoodDrawable(mood)),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Выбранное настроение",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = stringResource(mood.labelRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AIRecommendationCard(
    recommendation: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "🤖 AI Рекомендация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentYellow
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }

            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Text("✕", fontSize = 16.sp)
            }
        }
    }
}

private fun getMoodDrawable(mood: Mood): Int = when (mood) {
    Mood.VERY_SAD -> R.drawable.mood_very_sad
    Mood.SAD -> R.drawable.mood_sad
    Mood.SLIGHTLY_SAD -> R.drawable.mood_slightly_sad
    Mood.NEUTRAL -> R.drawable.mood_neutral
    Mood.SLIGHTLY_HAPPY -> R.drawable.mood_slightly_happy
    Mood.HAPPY -> R.drawable.mood_happy
    Mood.VERY_HAPPY -> R.drawable.mood_very_happy
}
