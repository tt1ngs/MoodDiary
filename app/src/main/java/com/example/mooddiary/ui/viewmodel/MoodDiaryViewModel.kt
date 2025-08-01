package com.example.mooddiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MoodDiaryViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodDiaryUiState())
    val uiState: StateFlow<MoodDiaryUiState> = _uiState.asStateFlow()

    val allMoodEntries = moodRepository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadTodayEntry()
    }

    fun saveMoodEntry(mood: Mood, note: String) {
        viewModelScope.launch {
            try {
                val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val sentimentScore = analyzeSentiment(note)
                val aiRecommendation = generateAIRecommendation(mood, sentimentScore, note)

                val entry = MoodEntry(
                    mood = mood,
                    note = note,
                    dateTime = currentDateTime,
                    sentimentScore = sentimentScore
                )

                moodRepository.insertEntry(entry)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Запись сохранена!",
                    aiRecommendation = aiRecommendation
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка сохранения: ${e.message}"
                )
            }
        }
    }

    fun getEntryById(entryId: Long): StateFlow<MoodEntry?> {
        return flow {
            emit(null) // Заглушка для компиляции
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            try {
                moodRepository.deleteEntry(entry)
                _uiState.value = _uiState.value.copy(
                    message = "Запись удалена"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка удаления: ${e.message}"
                )
            }
        }
    }

    fun getEntriesForDateRange(startDate: LocalDate, endDate: LocalDate): StateFlow<List<MoodEntry>> {
        return moodRepository.getEntriesBetweenDates(startDate, endDate)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    private fun loadTodayEntry() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val todayEntry = moodRepository.getEntryByDate(today)
            _uiState.value = _uiState.value.copy(
                todayEntry = todayEntry
            )
        }
    }

    // Улучшенный анализ тональности текста
    private fun analyzeSentiment(text: String): Float {
        if (text.isBlank()) return 0f

        val positiveWords = listOf(
            "хорошо", "отлично", "замечательно", "прекрасно", "счастлив", "радость",
            "весело", "удача", "успех", "любовь", "позитив", "классно", "супер",
            "восторг", "блаженство", "вдохновение", "энергия", "мотивация"
        )

        val negativeWords = listOf(
            "плохо", "ужасно", "грустно", "депрессия", "злость", "боль", "проблема",
            "стресс", "тревога", "печаль", "усталость", "разочарование", "беда",
            "паника", "страх", "одиночество", "отчаяние", "безнадежность"
        )

        val words = text.lowercase().split(" ", ",", ".", "!", "?", ";", ":")
        var score = 0f
        var wordCount = 0

        words.forEach { word ->
            when {
                positiveWords.any { word.contains(it) } -> {
                    score += 0.2f
                    wordCount++
                }
                negativeWords.any { word.contains(it) } -> {
                    score -= 0.2f
                    wordCount++
                }
            }
        }

        // Нормализация относительно количества эмоциональных слов
        return if (wordCount > 0) {
            (score / wordCount).coerceIn(-1f, 1f)
        } else {
            0f
        }
    }

    // Генерация AI-рекомендаций
    private fun generateAIRecommendation(mood: Mood, sentimentScore: Float?, note: String): String {
        return when {
            mood == Mood.VERY_SAD || mood == Mood.SAD -> {
                when {
                    sentimentScore != null && sentimentScore < -0.5f ->
                        "Замечен сильный негатив в записи. Попробуйте глубокое дыхание или прогулку на свежем воздухе. Помните: это временно."
                    note.contains("стресс", ignoreCase = true) ->
                        "Стресс может сильно влиять на настроение. Рассмотрите техники релаксации или медитацию."
                    note.contains("устал", ignoreCase = true) ->
                        "Усталость влияет на эмоции. Позаботьтесь о качественном сне и отдыхе."
                    else ->
                        "Грустные дни случаются с каждым. Попробуйте заняться любимым делом или связаться с близкими."
                }
            }
            mood == Mood.SLIGHTLY_SAD -> {
                "Легкая грусть - это нормально. Попробуйте послушать музыку или сделать что-то приятное для себя."
            }
            mood == Mood.NEUTRAL -> {
                "Нейтральное настроение - хорошая база для планирования дня. Возможно, стоит добавить что-то вдохновляющее?"
            }
            mood == Mood.SLIGHTLY_HAPPY || mood == Mood.HAPPY -> {
                when {
                    sentimentScore != null && sentimentScore > 0.3f ->
                        "Отличное настроение! Попробуйте поделиться позитивом с окружающими."
                    note.contains("успех", ignoreCase = true) ->
                        "Поздравляем с успехом! Не забывайте отмечать свои достижения."
                    else ->
                        "Хорошее настроение - отличная возможность для новых начинаний!"
                }
            }
            mood == Mood.VERY_HAPPY -> {
                "Потрясающее настроение! Запомните этот момент и то, что к нему привело. Поделитесь радостью с близкими!"
            }
            else -> "Спасибо за запись! Продолжайте отслеживать свое настроение для лучшего понимания себя."
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            error = null,
            aiRecommendation = null
        )
    }

    fun dismissAIRecommendation() {
        _uiState.value = _uiState.value.copy(
            aiRecommendation = null
        )
    }
}

data class MoodDiaryUiState(
    val isLoading: Boolean = false,
    val todayEntry: MoodEntry? = null,
    val message: String? = null,
    val error: String? = null,
    val aiRecommendation: String? = null
)
