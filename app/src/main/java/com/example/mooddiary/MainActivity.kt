package com.example.mooddiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mooddiary.ui.navigation.Screen
import com.example.mooddiary.ui.screens.*
import com.example.mooddiary.ui.theme.MoodDiaryTheme
import com.example.mooddiary.ui.viewmodel.MoodDiaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MoodDiaryTheme {
                MoodDiaryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDiaryApp() {
    val navController = rememberNavController()
    val viewModel: MoodDiaryViewModel = hiltViewModel()

    val allEntries by viewModel.allMoodEntries.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.message, uiState.error) {
        if (uiState.message != null || uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    NavigationItem(Screen.Home, "Главная", Icons.Default.Home),
                    NavigationItem(Screen.Calendar, "Календарь", Icons.Default.CalendarToday),
                    NavigationItem(Screen.Statistics, "Статистика", Icons.Default.BarChart),
                    NavigationItem(Screen.Settings, "Настройки", Icons.Default.Settings)
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSaveMoodEntry = { mood, note ->
                        viewModel.saveMoodEntry(mood, note)
                    }
                )
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(
                    moodEntries = allEntries,
                    onDateSelected = { date ->
                    }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    moodEntries = allEntries
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull() ?: 0L
                val entry = allEntries.find { it.id == entryId }

                DetailScreen(
                    moodEntry = entry,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { },
                    onDeleteClick = { entryToDelete ->
                        viewModel.deleteEntry(entryToDelete)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

data class NavigationItem(
    val screen: Screen,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
