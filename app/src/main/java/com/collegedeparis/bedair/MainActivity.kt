package com.collegedeparis.bedair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.collegedeparis.bedair.domain.model.UserRole
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.screens.*
import com.collegedeparis.bedair.ui.theme.BeDairTheme
import com.collegedeparis.bedair.viewmodel.AttendanceViewModel
import com.collegedeparis.bedair.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeDairTheme {
                BeDairApp()
            }
        }
    }
}

@Composable
fun BeDairApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController, authViewModel)
        }
        composable(Screen.StudentLogin.route) {
            LoginScreen(navController, UserRole.STUDENT, authViewModel)
        }
        composable(Screen.DelegateLogin.route) {
            LoginScreen(navController, UserRole.DELEGATE, authViewModel)
        }
        composable(Screen.ProfessorLogin.route) {
            LoginScreen(navController, UserRole.PROFESSOR, authViewModel)
        }
        composable(Screen.ProfessorClassSelection.route) {
            ProfessorClassSelectionScreen(navController, authViewModel, attendanceViewModel)
        }
        composable(Screen.StudentHome.route) {
            StudentHomeScreen(navController, authViewModel, attendanceViewModel)
        }
        composable(Screen.DelegateHome.route + "?classId={classId}") { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId")
            DelegateHomeScreen(navController, authViewModel, attendanceViewModel, classId)
        }
        composable(Screen.DelegateHome.route) {
            DelegateHomeScreen(navController, authViewModel, attendanceViewModel)
        }
    }
}
