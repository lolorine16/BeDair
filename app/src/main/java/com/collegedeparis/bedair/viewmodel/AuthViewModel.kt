package com.collegedeparis.bedair.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.collegedeparis.bedair.data.JsonRepository
import com.collegedeparis.bedair.domain.model.Student
import com.collegedeparis.bedair.domain.model.Teacher
import com.collegedeparis.bedair.domain.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = JsonRepository(application)
    
    private val _selectedRole = MutableStateFlow<UserRole?>(null)
    val selectedRole: StateFlow<UserRole?> = _selectedRole

    private val _loggedInStudent = MutableStateFlow<Student?>(null)
    val loggedInStudent: StateFlow<Student?> = _loggedInStudent

    private val _loggedInTeacher = MutableStateFlow<Teacher?>(null)
    val loggedInTeacher: StateFlow<Teacher?> = _loggedInTeacher

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun selectRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun loginStudent(matricule: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            val students = repository.getStudents()
            Log.d("BeDairAuth", "Students loaded: ${students.size}")
            
            val student = students.find { 
                val match = it.matricule.trim().equals(matricule.trim(), ignoreCase = true)
                Log.d("BeDairAuth", "Comparing '${it.matricule.trim()}' with '${matricule.trim()}' -> $match")
                match
            }
            
            _isLoading.value = false
            if (student != null) {
                Log.d("BeDairAuth", "Login success for: ${student.matricule}")
                _loggedInStudent.value = student
                onResult(true)
            } else {
                Log.w("BeDairAuth", "Login failed for matricule: '$matricule'. Students in list: ${students.joinToString { it.matricule }}")
                onResult(false)
            }
        }
    }

    fun loginDelegate(matricule: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            val student = repository.getStudents().find { 
                it.matricule.trim().equals(matricule.trim(), ignoreCase = true) && it.estDelegue 
            }
            _isLoading.value = false
            if (student != null && password == "1234") {
                _loggedInStudent.value = student
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun loginProfessor(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            val teacher = repository.getTeachers().find { 
                it.email.trim().equals(email.trim(), ignoreCase = true) && it.password == password 
            }
            _isLoading.value = false
            if (teacher != null) {
                _loggedInTeacher.value = teacher
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}
