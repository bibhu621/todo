package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Todo
import com.example.storage.TodoLocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TodoFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val filter: TodoFilter = TodoFilter.ALL,
    val searchQuery: String = "",
    val editingTodo: Todo? = null,
    val todoToDelete: Todo? = null,
    val showClearCompletedDialog: Boolean = false
) {
    val totalCount: Int
        get() = todos.size

    val activeCount: Int
        get() = todos.count { !it.isCompleted }

    val completedCount: Int
        get() = todos.count { it.isCompleted }

    val filteredTodos: List<Todo>
        get() {
            val list = when (filter) {
                TodoFilter.ALL -> todos
                TodoFilter.ACTIVE -> todos.filter { !it.isCompleted }
                TodoFilter.COMPLETED -> todos.filter { it.isCompleted }
            }
            return if (searchQuery.isBlank()) {
                list
            } else {
                list.filter { it.text.contains(searchQuery.trim(), ignoreCase = true) }
            }
        }

    val progress: Float
        get() = if (todos.isEmpty()) 0f else completedCount.toFloat() / totalCount.toFloat()
}

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = TodoLocalStorage(application.applicationContext)
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        loadTodos()
    }

    private fun loadTodos() {
        val loaded = storage.loadTodos()
        _uiState.update { it.copy(todos = loaded) }
    }

    private fun persist(todos: List<Todo>) {
        storage.saveTodos(todos)
    }

    fun addTodo(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val newTodo = Todo(text = trimmed)
        val updatedList = listOf(newTodo) + _uiState.value.todos
        _uiState.update { it.copy(todos = updatedList) }
        persist(updatedList)
    }

    fun toggleTodo(id: String) {
        val updatedList = _uiState.value.todos.map { todo ->
            if (todo.id == id) {
                todo.copy(isCompleted = !todo.isCompleted)
            } else {
                todo
            }
        }
        _uiState.update { it.copy(todos = updatedList) }
        persist(updatedList)
    }

    fun startEditing(todo: Todo) {
        _uiState.update { it.copy(editingTodo = todo) }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(editingTodo = null) }
    }

    fun saveEditing(id: String, newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isEmpty()) {
            // If edited to empty, cancel or delete
            cancelEditing()
            return
        }
        val updatedList = _uiState.value.todos.map { todo ->
            if (todo.id == id) {
                todo.copy(text = trimmed)
            } else {
                todo
            }
        }
        _uiState.update { it.copy(todos = updatedList, editingTodo = null) }
        persist(updatedList)
    }

    fun requestDelete(todo: Todo) {
        _uiState.update { it.copy(todoToDelete = todo) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(todoToDelete = null) }
    }

    fun confirmDelete() {
        val toDelete = _uiState.value.todoToDelete ?: return
        val updatedList = _uiState.value.todos.filterNot { it.id == toDelete.id }
        _uiState.update { it.copy(todos = updatedList, todoToDelete = null) }
        persist(updatedList)
    }

    fun directDelete(id: String) {
        val updatedList = _uiState.value.todos.filterNot { it.id == id }
        _uiState.update { it.copy(todos = updatedList) }
        persist(updatedList)
    }

    fun setFilter(filter: TodoFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun requestClearCompleted() {
        _uiState.update { it.copy(showClearCompletedDialog = true) }
    }

    fun cancelClearCompleted() {
        _uiState.update { it.copy(showClearCompletedDialog = false) }
    }

    fun confirmClearCompleted() {
        val updatedList = _uiState.value.todos.filterNot { it.isCompleted }
        _uiState.update { it.copy(todos = updatedList, showClearCompletedDialog = false) }
        persist(updatedList)
    }
}
