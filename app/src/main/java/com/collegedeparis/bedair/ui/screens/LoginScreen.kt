package com.collegedeparis.bedair.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.collegedeparis.bedair.domain.model.UserRole
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.components.BeDairButton
import com.collegedeparis.bedair.ui.components.BeDairLogo
import com.collegedeparis.bedair.ui.components.BeDairTextField
import com.collegedeparis.bedair.ui.theme.PrimaryGreen
import com.collegedeparis.bedair.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    role: UserRole,
    viewModel: AuthViewModel = viewModel()
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val icon = when (role) {
        UserRole.DELEGATE -> Icons.Default.Person
        UserRole.STUDENT -> Icons.Default.People
        UserRole.PROFESSOR -> Icons.Default.Flag
    }

    val placeholder = when (role) {
        UserRole.DELEGATE -> "Matricule Délégué"
        UserRole.STUDENT -> "Matricule Étudiant"
        UserRole.PROFESSOR -> "Email Professionnel"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BeDairLogo(logoSize = 100, showText = false)

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Identification",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                BeDairTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    placeholder = placeholder
                )

                if (role != UserRole.STUDENT) {
                    Spacer(modifier = Modifier.height(16.dp))
                    BeDairTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Mot de passe",
                        isPassword = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            BeDairButton(
                text = if (isLoading) "Chargement..." else "Se Connecter",
                onClick = {
                    if (identifier.isBlank()) {
                        Toast.makeText(context, "Veuillez remplir les champs", Toast.LENGTH_SHORT).show()
                        return@BeDairButton
                    }

                    val onResult: (Boolean) -> Unit = { success ->
                        if (success) {
                            when (role) {
                                UserRole.STUDENT -> navController.navigate(Screen.StudentHome.route)
                                UserRole.DELEGATE -> navController.navigate(Screen.DelegateHome.route)
                                UserRole.PROFESSOR -> navController.navigate(Screen.ProfessorClassSelection.route)
                            }
                        } else {
                            Toast.makeText(context, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                        }
                    }

                    when (role) {
                        UserRole.STUDENT -> viewModel.loginStudent(identifier, onResult)
                        UserRole.DELEGATE -> viewModel.loginDelegate(identifier, password, onResult)
                        UserRole.PROFESSOR -> viewModel.loginProfessor(identifier, password, onResult)
                    }
                },
                containerColor = if (isLoading) PrimaryGreen.copy(alpha = 0.5f) else PrimaryGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = PaddingValues(0.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        }
    }
}
