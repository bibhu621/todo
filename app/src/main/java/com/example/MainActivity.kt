package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TodoScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: TodoViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()

        TodoScreen(
          uiState = uiState,
          onAddTodo = viewModel::addTodo,
          onToggleTodo = viewModel::toggleTodo,
          onStartEdit = viewModel::startEditing,
          onSaveEdit = viewModel::saveEditing,
          onCancelEdit = viewModel::cancelEditing,
          onRequestDelete = viewModel::requestDelete,
          onConfirmDelete = viewModel::confirmDelete,
          onCancelDelete = viewModel::cancelDelete,
          onSetFilter = viewModel::setFilter,
          onSearchQueryChange = viewModel::setSearchQuery,
          onRequestClearCompleted = viewModel::requestClearCompleted,
          onConfirmClearCompleted = viewModel::confirmClearCompleted,
          onCancelClearCompleted = viewModel::cancelClearCompleted,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

