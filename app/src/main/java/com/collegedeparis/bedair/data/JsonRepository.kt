package com.collegedeparis.bedair.data

import android.content.Context
import com.collegedeparis.bedair.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

import android.util.Log

class JsonRepository(private val context: Context) {
    private val gson = Gson()

    private fun <T> loadData(fileName: String, typeToken: TypeToken<T>): T {
        val file = File(context.filesDir, fileName)
        
        val jsonString = try {
            if (file.exists() && file.length() > 0 && !fileName.equals("attendance.json")) {
                Log.d("BeDairDB", "Forcing refresh from assets for $fileName")
                context.assets.open(fileName).bufferedReader().use { it.readText() }.also {
                    file.writeText(it)
                }
            } else if (file.exists() && file.length() > 0) {
                Log.d("BeDairDB", "Loading $fileName from internal storage")
                file.readText()
            } else {
                Log.d("BeDairDB", "Loading $fileName from assets (first time)")
                context.assets.open(fileName).bufferedReader().use { it.readText() }.also {
                    file.writeText(it)
                }
            }
        } catch (e: Exception) {
            Log.e("BeDairDB", "Error reading $fileName", e)
            "[]"
        }

        Log.d("BeDairDB", "JSON for $fileName: ${jsonString.take(50)}...")
        
        val result = try {
            gson.fromJson<T>(jsonString, typeToken.type)
        } catch (e: Exception) {
            Log.e("BeDairDB", "Error parsing JSON from $fileName: $jsonString", e)
            gson.fromJson("[]", typeToken.type)
        }
        
        if (result is List<*>) {
            Log.d("BeDairDB", "Loaded list of size ${result.size} for $fileName")
        }
        return result
    }

    private fun <T> saveData(fileName: String, data: T) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    fun getStudents(): List<Student> = loadData("students.json", object : TypeToken<List<Student>>() {})
    fun getTeachers(): List<Teacher> = loadData("teachers.json", object : TypeToken<List<Teacher>>() {})
    fun getSubjects(): List<Subject> = loadData("subjects.json", object : TypeToken<List<Subject>>() {})
    fun getSessions(): List<Session> = loadData("sessions.json", object : TypeToken<List<Session>>() {})
    fun getAttendances(): List<Attendance> = loadData("attendance.json", object : TypeToken<List<Attendance>>() {})
    fun getClasses(): List<Class> = loadData("classes.json", object : TypeToken<List<Class>>() {})

    fun saveAttendances(attendances: List<Attendance>) = saveData("attendance.json", attendances)
    fun saveSessions(sessions: List<Session>) = saveData("sessions.json", sessions)
}
