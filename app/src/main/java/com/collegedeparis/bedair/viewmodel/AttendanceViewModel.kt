package com.collegedeparis.bedair.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.collegedeparis.bedair.data.JsonRepository
import com.collegedeparis.bedair.domain.model.*

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = JsonRepository(application)
    
    private val _attendances = mutableStateListOf<Attendance>()
    val attendances: List<Attendance> = _attendances

    private val _sessions = mutableStateListOf<Session>()
    val sessions: List<Session> = _sessions

    init {
        _attendances.addAll(repository.getAttendances())
        _sessions.addAll(repository.getSessions())
    }

    fun getStudentAttendance(studentId: Int): List<Attendance> {
        return _attendances.filter { it.studentId == studentId }
    }

    fun getSessionsForClass(classId: String): List<Session> {
        return _sessions.filter { it.classId == classId }
    }

    fun getSessionsForTeacher(teacherId: Int): List<Session> {
        return _sessions.filter { it.teacherId == teacherId }
    }

    fun sendListToProfessor(sessionId: Int) {
        val index = _sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            _sessions[index] = _sessions[index].copy(isSentByDelegate = true)
            repository.saveSessions(_sessions)
        }
    }

    fun approveListAsTeacher(sessionId: Int) {
        val index = _sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            _sessions[index] = _sessions[index].copy(isApprovedByTeacher = true)
            repository.saveSessions(_sessions)
            // Synchroniser le fichier attendance.json si nécessaire
            repository.saveAttendances(_attendances)
        }
    }

    fun getStudentsForClass(classId: String): List<Student> {
        return repository.getStudents().filter { it.classeId == classId }
    }

    fun markAttendance(studentId: Int, sessionId: Int, status: String) {
        val index = _attendances.indexOfFirst { it.studentId == studentId && it.sessionId == sessionId }
        
        if (index != -1) {
            _attendances[index] = _attendances[index].copy(status = status)
        } else {
            val nextId = (_attendances.maxOfOrNull { it.id } ?: 0) + 1
            _attendances.add(Attendance(nextId, studentId, sessionId, status))
        }
        repository.saveAttendances(_attendances)
        // Log pour débogage
        android.util.Log.d("BeDairAttendance", "Marked student $studentId in session $sessionId as $status")
    }

    fun getSubjectName(subjectId: Int): String {
        return repository.getSubjects().find { it.id == subjectId }?.nom ?: "Inconnu"
    }

    fun getAbsenceHours(studentId: Int): Int {
        // Chaque session dure 4h. 
        // Absent (A) = 4h d'absence
        // Retard (R) = 2h d'absence
        // Présent (P) = 0h d'absence
        return _attendances.filter { it.studentId == studentId }.sumOf { 
            when (it.status) {
                "A" -> 4
                "R" -> 2
                else -> 0
            }
        }
    }
}
