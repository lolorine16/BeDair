package com.collegedeparis.bedair.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.collegedeparis.bedair.domain.model.UserRole
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.components.BeDairButton
import com.collegedeparis.bedair.ui.components.BeDairLogo
import com.collegedeparis.bedair.ui.components.RoleOption
import com.collegedeparis.bedair.ui.theme.PrimaryGreen

import androidx.lifecycle.viewmodel.compose.viewModel
import com.collegedeparis.bedair.viewmodel.AuthViewModel

@Composable
fun RoleSelectionScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val selectedRole by viewModel.selectedRole.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        BeDairLogo()

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choisissez une option",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleOption(
                    role = "Délégué",
                    icon = Icons.Default.Person,
                    isSelected = selectedRole == UserRole.DELEGATE,
                    onClick = { viewModel.selectRole(UserRole.DELEGATE) }
                )
                RoleOption(
                    role = "Étudiants",
                    icon = Icons.Default.People,
                    isSelected = selectedRole == UserRole.STUDENT,
                    onClick = { viewModel.selectRole(UserRole.STUDENT) }
                )
                RoleOption(
                    role = "Professeur",
                    icon = Icons.Default.Flag,
                    isSelected = selectedRole == UserRole.PROFESSOR,
                    onClick = { viewModel.selectRole(UserRole.PROFESSOR) }
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        BeDairButton(
            text = "Valider",
            onClick = {
                when (selectedRole) {
                    UserRole.DELEGATE -> navController.navigate(Screen.DelegateLogin.route)
                    UserRole.STUDENT -> navController.navigate(Screen.StudentLogin.route)
                    UserRole.PROFESSOR -> navController.navigate(Screen.ProfessorLogin.route)
                    null -> {}
                }
            },
            containerColor = if (selectedRole != null) PrimaryGreen else MaterialTheme.colorScheme.secondary,
            contentColor = if (selectedRole != null) Color.White else Color.Gray
        )
    }
}
