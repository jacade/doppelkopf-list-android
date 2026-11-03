package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.DoppelkopfDatabase
import com.example.data.repository.DoppelkopfRepository
import com.example.ui.DoppelkopfScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DoppelkopfViewModel
import com.example.viewmodel.DoppelkopfViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: DoppelkopfViewModel by viewModels {
        val database = DoppelkopfDatabase.getInstance(applicationContext)
        val repository = DoppelkopfRepository(database.dao())
        DoppelkopfViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DoppelkopfScreen(viewModel = viewModel)
            }
        }
    }
}
