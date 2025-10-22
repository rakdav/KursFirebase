package com.example.kursproject.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kursproject.models.Task
import com.example.kursproject.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    task: Task?=null,
    viewModel: TaskViewModel= viewModel(),
    onBack: () -> Unit
)
{
    var showDatePicker by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(task?.title?:"") }
    var description by remember { mutableStateOf(task?.description?:"") }
    var priority by remember { mutableStateOf(task?.priority ?: Task.Priority.MEDIUM) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it)}?:task?.dueDate?:"";
    var dueDate by remember { mutableStateOf(task?.dueDate?:"") }
    val isEditing = task != null

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Редактировать задачу" else "Новая задача") },
                navigationIcon = {
                    IconButton (onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton (
                onClick = {
                    if (title.isNotBlank()) {
                        if (isEditing) {
                            viewModel.updateTask(
                                task!!.copy(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    dueDate = dueDate
                                )
                            )
                        } else {
                            viewModel.addTask(title, description, priority)
                        }
                        onBack()
                    }
                },
            ) {
                Icon(
                    if (isEditing) Icons.Filled.Done else Icons.Default.Add,
                    contentDescription = if (isEditing) "Сохранить" else "Добавить"
                )
            }
        }
    )
    {

    }
}

fun convertMillisToDate(millis: Long?): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis!!))
}