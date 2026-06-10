package com.collegedeparis.bedair.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object RoleSelection : Screen("role_selection")
    object StudentLogin : Screen("student_login")
    object DelegateLogin : Screen("delegate_login")
    object ProfessorLogin : Screen("professor_login")
    object ProfessorClassSelection : Screen("professor_class_selection")
    object StudentHome : Screen("student_home")
    object DelegateHome : Screen("delegate_home")
}
