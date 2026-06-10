package com.collegedeparis.bedair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.collegedeparis.bedair.domain.model.Student
import com.collegedeparis.bedair.domain.model.UserRole
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.theme.ErrorRed
import com.collegedeparis.bedair.ui.theme.PrimaryGreen
import com.collegedeparis.bedair.ui.theme.SecondaryGrey
import com.collegedeparis.bedair.viewmodel.AttendanceViewModel
import com.collegedeparis.bedair.viewmodel.AuthViewModel

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegateHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel(),
    classId: String? = null
) {
    val loggedInStudent = authViewModel.loggedInStudent.collectAsState().value
    val loggedInTeacher = authViewModel.loggedInTeacher.collectAsState().value
    val selectedRole by authViewModel.selectedRole.collectAsState()
    
    val currentClassId = classId ?: loggedInStudent?.classeId ?: "B2B-IT"
    
    // Date Management
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    
    val weekDays = remember {
        val days = mutableListOf<LocalDate>()
        var current = today
        // Get current week (Monday to Friday)
        while (current.dayOfWeek.value > 1) current = current.minusDays(1)
        repeat(5) {
            days.add(current)
            current = current.plusDays(1)
        }
        days
    }

    val students = attendanceViewModel.getStudentsForClass(currentClassId)
    val allSessions = if (selectedRole == UserRole.PROFESSOR && loggedInTeacher != null) {
        attendanceViewModel.getSessionsForTeacher(loggedInTeacher.id).filter { it.classId == currentClassId }
    } else {
        attendanceViewModel.getSessionsForClass(currentClassId)
    }
    
    // Filter sessions by selected date
    val sessionsOnSelectedDate = allSessions.filter { it.date == selectedDate.toString() }
    
    var selectedSession by remember(selectedDate) { mutableStateOf(sessionsOnSelectedDate.firstOrNull()) }
    val currentSession = attendanceViewModel.sessions.find { it.id == selectedSession?.id }
    
    // Modification : Seul le jour actuel est modifiable
    val isReadOnly = selectedDate != today

    val displayName = loggedInStudent?.let { "${it.prenom} ${it.nom}" } 
        ?: loggedInTeacher?.let { "${it.prenom} ${it.nom}" } 
        ?: "Utilisateur"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(displayName, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        val subTitle = if (selectedRole == UserRole.PROFESSOR) "Professeur - $currentClassId" else currentClassId
                        Text(subTitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
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
                .background(Color.White)
        ) {
            // Date Selector (Top Scrollable Row)
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(weekDays) { date ->
                    val isPast = date.toEpochDay() < today.toEpochDay()
                    val isFuture = date.toEpochDay() > today.toEpochDay()
                    val isSelected = date == selectedDate
                    
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = date },
                        label = { 
                            Text(date.format(DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH))) 
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (date == today) PrimaryGreen else Color.Gray,
                            selectedLabelColor = Color.White,
                            containerColor = if (isPast || isFuture) SecondaryGrey.copy(alpha = 0.5f) else SecondaryGrey
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isPast || isFuture) Color.LightGray else Color.Transparent,
                            selectedBorderColor = if (date == today) PrimaryGreen else Color.Gray,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            // Session Selector for the day
            if (sessionsOnSelectedDate.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sessionsOnSelectedDate) { session ->
                        FilterChip(
                            selected = currentSession?.id == session.id,
                            onClick = { selectedSession = session },
                            label = { Text("${attendanceViewModel.getSubjectName(session.subjectId)} (${session.startTime})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isReadOnly) Color.Gray else PrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucun cours ce jour", color = Color.Gray)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentSession != null) {
                    item {
                        val buttonText = when {
                            selectedRole == UserRole.PROFESSOR -> {
                                if (currentSession.isApprovedByTeacher) "Liste Approuvée" 
                                else if (currentSession.isSentByDelegate) "Approuver la liste"
                                else "Attente envoi délégué"
                            }
                            else -> {
                                if (currentSession.isApprovedByTeacher) "Liste Validée par Prof"
                                else if (currentSession.isSentByDelegate) "Liste Envoyée"
                                else "Envoyer la liste au professeur"
                            }
                        }
                        
                        val isEnabled = !isReadOnly && when {
                            selectedRole == UserRole.PROFESSOR -> currentSession.isSentByDelegate && !currentSession.isApprovedByTeacher
                            else -> !currentSession.isSentByDelegate && !currentSession.isApprovedByTeacher
                        }

                        CourseHeader(
                            title = attendanceViewModel.getSubjectName(currentSession.subjectId),
                            time = "${currentSession.startTime} - ${currentSession.endTime}",
                            buttonText = if (isReadOnly) "Consultation Uniquement" else buttonText,
                            isEnabled = isEnabled,
                            onButtonClick = {
                                if (selectedRole == UserRole.PROFESSOR) {
                                    attendanceViewModel.approveListAsTeacher(currentSession.id)
                                } else {
                                    attendanceViewModel.sendListToProfessor(currentSession.id)
                                }
                            }
                        )
                    }

                    items(students) { student ->
                        val attendance = attendanceViewModel.attendances.find { 
                            it.studentId == student.id && it.sessionId == currentSession.id 
                        }
                        
                        StudentAttendanceItem(
                            studentName = "${student.prenom} ${student.nom}",
                            matricule = student.matricule,
                            status = attendance?.status,
                            isReadOnly = isReadOnly || (selectedRole != UserRole.PROFESSOR && currentSession.isSentByDelegate) || currentSession.isApprovedByTeacher,
                            onStatusChange = { newStatus ->
                                if (!isReadOnly) {
                                    attendanceViewModel.markAttendance(student.id, currentSession.id, newStatus)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseHeader(
    title: String, 
    time: String, 
    buttonText: String, 
    isEnabled: Boolean,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryGrey)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(time, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onButtonClick,
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(buttonText, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun StudentAttendanceItem(
    studentName: String,
    matricule: String,
    status: String?,
    isReadOnly: Boolean = false,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryGrey)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(studentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(matricule, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Présent
                AttendanceButton(
                    icon = Icons.Default.Check,
                    color = if (status == "P") PrimaryGreen else Color.LightGray,
                    isEnabled = !isReadOnly,
                    onClick = { onStatusChange("P") }
                )
                // Retard
                AttendanceButton(
                    icon = Icons.Default.AccessTime,
                    color = if (status == "R") Color(0xFFFFA000) else Color.LightGray,
                    isEnabled = !isReadOnly,
                    onClick = { onStatusChange("R") }
                )
                // Absent
                AttendanceButton(
                    icon = Icons.Default.Close,
                    color = if (status == "A") ErrorRed else Color.LightGray,
                    isEnabled = !isReadOnly,
                    onClick = { onStatusChange("A") }
                )
            }
        }
    }
}

@Composable
fun AttendanceButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(32.dp),
        color = if (isEnabled) color else color.copy(alpha = 0.3f),
        shape = CircleShape,
        onClick = if (isEnabled) onClick else ({})
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}
