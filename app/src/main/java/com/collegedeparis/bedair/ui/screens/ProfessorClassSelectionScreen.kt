package com.collegedeparis.bedair.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.theme.PrimaryGreen
import com.collegedeparis.bedair.ui.theme.SecondaryGrey
import com.collegedeparis.bedair.viewmodel.AttendanceViewModel
import com.collegedeparis.bedair.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorClassSelectionScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    val teacher = authViewModel.loggedInTeacher.collectAsState().value
    
    if (teacher == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.RoleSelection.route)
        }
        return
    }

    // On récupère les classes associées aux sessions du professeur
    val classes = attendanceViewModel.getSessionsForTeacher(teacher.id)
        .map { it.classId }
        .distinct()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bienvenue, Dr. ${teacher.nom}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(start = 12.dp).size(32.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.RoleSelection.route) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sélectionnez une classe",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(classes) { classId ->
                    Card(
                        onClick = {
                            // On pourrait stocker la classe sélectionnée dans un StateFlow si besoin
                            // Pour l'instant on passe par la route ou on laisse DelegateHome filtrer
                            navController.navigate(Screen.DelegateHome.route + "?classId=$classId")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SecondaryGrey)
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = classId,
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }
            
            if (classes.isEmpty()) {
                Text(
                    text = "Aucune session assignée",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        }
    }
}
