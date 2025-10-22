package com.example.kursproject.models

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    enum class Priority {
        LOW, MEDIUM, HIGH
    }

    companion object {
        const val PRIORITY_LOW = "LOW"
        const val PRIORITY_MEDIUM = "MEDIUM"
        const val PRIORITY_HIGH = "HIGH"
    }
}