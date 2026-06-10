package com.collegedeparis.bedair.domain.model

enum class UserRole {
    DELEGATE, STUDENT, PROFESSOR
}

data class Student(
    val id: Int,
    val matricule: String,
    val nom: String,
    val prenom: String,
    val classeId: String,
    val estDelegue: Boolean = false
)

data class Teacher(
    val id: Int,
    val nom: String,
    val prenom: String,
    val email: String,
    val password: String
)

data class Subject(
    val id: Int,
    val nom: String
)

data class Session(
    val id: Int,
    val date: String,
    val teacherId: Int,
    val subjectId: Int,
    val classId: String,
    val startTime: String,
    val endTime: String,
    val isSentByDelegate: Boolean = false,
    val isApprovedByTeacher: Boolean = false
)

data class Attendance(
    val id: Int,
    val studentId: Int,
    val sessionId: Int,
    val status: String // "P" for Present, "A" for Absent, "R" for Retard
)

data class Class(
    val id: String,
    val nom: String
)
