package com.example.mooddiary.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(entryId: Long) = "detail/$entryId"
    }
    object DayDetail : Screen("day_detail/{year}/{month}/{day}") {
        fun createRoute(year: Int, month: Int, day: Int) = "day_detail/$year/$month/$day"
    }
}
