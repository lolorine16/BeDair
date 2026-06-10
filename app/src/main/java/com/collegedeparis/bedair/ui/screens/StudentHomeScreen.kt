package com.collegedeparis.bedair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.collegedeparis.bedair.navigation.Screen
import com.collegedeparis.bedair.ui.theme.PrimaryGreen
import com.collegedeparis.bedair.ui.theme.SecondaryGrey
import com.collegedeparis.bedair.viewmodel.AttendanceViewModel
import com.collegedeparis.bedair.viewmodel.AuthViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    val student = authViewModel.loggedInStudent.collectAsState().value
    
    if (student == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.RoleSelection.route)
        }
        return
    }

    // Date Management
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    
    val weekDays = remember {
        val days = mutableListOf<LocalDate>()
        var current = today
        while (current.dayOfWeek.value > 1) current = current.minusDays(1)
        repeat(5) {
            days.add(current)
            current = current.plusDays(1)
        }
        days
    }

    val studentSessions = attendanceViewModel.getSessionsForClass(student.classeId)
        .filter { it.date == selectedDate.toString() }
    
    val totalAbsenceHours = attendanceViewModel.getAbsenceHours(student.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${student.prenom} ${student.nom}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = student.classeId,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
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
                    val isSelected = date == selectedDate
                    val isToday = date == today
                    
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = date },
                        label = { 
                            Text(date.format(DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH))) 
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isToday) PrimaryGreen else Color.Gray,
                            selectedLabelColor = Color.White,
                            containerColor = if (date != today) SecondaryGrey.copy(alpha = 0.5f) else SecondaryGrey
                        )
                    )
                }
            }

            // Absence Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryGrey)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Absences (Semaine)", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "$totalAbsenceHours Heures",
                            style = MaterialTheme.typography.headlineSmall,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                "Mes Cours du ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH))}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            if (studentSessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun cours prévu", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(studentSessions) { session ->
                        val attendance = attendanceViewModel.getStudentAttendance(student.id)
                            .find { it.sessionId == session.id }
                        
                        CourseStatusCard(
                            title = attendanceViewModel.getSubjectName(session.subjectId),
                            time = "${session.startTime} - ${session.endTime}",
                            status = when (attendance?.status) {
                                "P" -> "Présent"
                                "A" -> "Absent"
                                "R" -> "Retard"
                                else -> "Non marqué"
                            },
                            statusColor = when (attendance?.status) {
                                "P" -> PrimaryGreen
                                "A" -> Color.Red
                                "R" -> Color(0xFFFFA000)
                                else -> Color.Gray
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseStatusCard(title: String, time: String, status: String, statusColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryGrey)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                status,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
