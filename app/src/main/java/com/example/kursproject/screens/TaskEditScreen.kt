package com.example.kursproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
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
        padding->
        Column (
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        )
        {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название задачи") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Приоритет:", style = MaterialTheme.typography.titleSmall)
            Row (modifier = Modifier.padding(vertical = 8.dp)) {
                Task.Priority.entries.forEach { p ->
                    val isSelected = p == priority
                    Text(
                        text = when (p) {
                            Task.Priority.LOW -> "Низкий"
                            Task.Priority.MEDIUM -> "Средний"
                            Task.Priority.HIGH -> "Высокий"
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { priority = p }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {  },
                label = { Text("Срок выполнения (DD-MM-YYYY)") },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            )
            if (showDatePicker) {
                Popup (
                    onDismissRequest = { showDatePicker = false },
                    alignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 64.dp)
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        DatePickerDialog (
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton (onClick = {
                                    showDatePicker=false
                                    dueDate=selectedDate
                                })
                                {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }

                        )
                        {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long?): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis!!))
}