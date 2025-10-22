package com.example.kursproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursproject.models.Task
import com.example.kursproject.repositiories.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel: ViewModel() {
    private val repository = TaskRepository()
    private val _tasks = MutableLiveData<List<Task>>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String?>()
    private val currentTask = MutableStateFlow<Task?>(null)

    val tasks: LiveData<List<Task>> = _tasks
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMessage: LiveData<String?> = _errorMessage
    val task: StateFlow<Task?> = currentTask.asStateFlow()

    init {
        loadTasks()
        observeTasks()
    }
    fun getTask(id:String){
        viewModelScope.launch {
            currentTask.value= repository.getTaskById(id)!!
        }
    }
    private fun observeTasks() {
        repository.observeTasks().observeForever { tasksList ->
            _tasks.value = tasksList
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _tasks.value = repository.getAllTasks()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки задач: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addTask(title: String, description: String, priority: Task.Priority) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newTask = Task(
                    title = title,
                    description = description,
                    priority = priority
                )
                repository.addTask(newTask)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка добавления задачи: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления задачи: ${e.message}"
            }
        }
    }
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления задачи: ${e.message}"
            }
        }
    }
    fun toggleTaskCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        updateTask(updatedTask)
    }
    fun clearError() {
        _errorMessage.value = null
    }
}