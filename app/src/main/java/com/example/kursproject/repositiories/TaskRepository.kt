package com.example.kursproject.repositiories

import androidx.lifecycle.LiveData
import com.example.kursproject.models.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TaskRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val tasksRef: DatabaseReference = database.getReference("tasks")
    // Create
    suspend fun addTask(task: Task): String {
        val id = tasksRef.push().key ?: UUID.randomUUID().toString()
        val taskWithId = task.copy(
            id = id,
            createdAt = getCurrentDateTime(),
            updatedAt = getCurrentDateTime()
        )
        tasksRef.child(id).setValue(taskWithId).await()
        return id
    }

    // Read - все задачи
    suspend fun getAllTasks(): List<Task> {
        return tasksRef.get().await().children.map { snapshot ->
            snapshot.getValue(Task::class.java) ?: Task()
        }
    }

    // Read - по ID
    suspend fun getTaskById(id: String): Task? {
        return tasksRef.child(id).get().await().getValue(Task::class.java)
    }

    // Update
    suspend fun updateTask(task: Task) {
        val updatedTask = task.copy(updatedAt = getCurrentDateTime())
        tasksRef.child(task.id).setValue(updatedTask).await()
    }

    // Delete
    suspend fun deleteTask(taskId: String) {
        tasksRef.child(taskId).removeValue().await()
    }

    // LiveData для наблюдения за изменениями
    fun observeTasks(): LiveData<List<Task>> {
        return object : LiveData<List<Task>>() {
            private val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                    postValue(tasks)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки
                }
            }

            override fun onActive() {
                tasksRef.addValueEventListener(valueEventListener)
            }

            override fun onInactive() {
                tasksRef.removeEventListener(valueEventListener)
            }
        }
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
    }
}