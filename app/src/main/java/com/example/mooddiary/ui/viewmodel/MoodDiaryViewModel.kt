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

                val entry = MoodEntry(
                    mood = mood,
                    note = note,
                    dateTime = currentDateTime,
                    sentimentScore = sentimentScore
                )

                moodRepository.insertEntry(entry)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Запись сохранена!"
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

    // Простой анализ тональности текста
    private fun analyzeSentiment(text: String): Float {
        if (text.isBlank()) return 0f

        val positiveWords = listOf(
            "хорошо", "отлично", "замечательно", "прекрасно", "счастлив", "радость",
            "весело", "удача", "успех", "любовь", "позитив", "классно", "супер"
        )

        val negativeWords = listOf(
            "плохо", "ужасно", "грустно", "депрессия", "злость", "боль", "проблема",
            "стресс", "тревога", "печаль", "усталость", "разочарование", "беда"
        )

        val words = text.lowercase().split(" ", ",", ".", "!", "?", ";", ":")
        var score = 0f

        words.forEach { word ->
            when {
                positiveWords.any { word.contains(it) } -> score += 0.1f
                negativeWords.any { word.contains(it) } -> score -= 0.1f
            }
        }

        return score.coerceIn(-1f, 1f)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            error = null
        )
    }
}

data class MoodDiaryUiState(
    val isLoading: Boolean = false,
    val todayEntry: MoodEntry? = null,
    val message: String? = null,
    val error: String? = null
)
