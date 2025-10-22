package com.example.kursproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kursproject.screens.TaskListScreen
import com.example.kursproject.ui.theme.KursProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KursProjectTheme {
                val navController= rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "taskList"
                )
                {
                    composable ("taskList") {
                        TaskListScreen (
                            onAddTask = {
                                navController.navigate("taskEdit")
                            },
                            onEditTask = { task ->
                                navController.navigate("taskEdit/${task.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KursProjectTheme {
        Greeting("Android")
    }
}