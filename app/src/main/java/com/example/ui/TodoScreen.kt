package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Todo
import com.example.viewmodel.TodoFilter
import com.example.viewmodel.TodoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    uiState: TodoUiState,
    onAddTodo: (String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onStartEdit: (Todo) -> Unit,
    onSaveEdit: (String, String) -> Unit,
    onCancelEdit: () -> Unit,
    onRequestDelete: (Todo) -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onSetFilter: (TodoFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRequestClearCompleted: () -> Unit,
    onConfirmClearCompleted: () -> Unit,
    onCancelClearCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newTodoText by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TodoHeader(
                uiState = uiState,
                isSearchExpanded = isSearchExpanded,
                onToggleSearch = {
                    isSearchExpanded = !isSearchExpanded
                    if (!isSearchExpanded) onSearchQueryChange("")
                },
                onSearchQueryChange = onSearchQueryChange,
                onClearCompletedClick = onRequestClearCompleted
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Pills Row
            TodoFilterRow(
                currentFilter = uiState.filter,
                totalCount = uiState.totalCount,
                activeCount = uiState.activeCount,
                completedCount = uiState.completedCount,
                onSelectFilter = onSetFilter,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Todo List or Empty State
            if (uiState.filteredTodos.isEmpty()) {
                TodoEmptyState(
                    filter = uiState.filter,
                    hasAnyTodos = uiState.todos.isNotEmpty(),
                    searchQuery = uiState.searchQuery,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("todo_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.filteredTodos,
                        key = { it.id }
                    ) { todo ->
                        TodoCard(
                            todo = todo,
                            onToggle = { onToggleTodo(todo.id) },
                            onEdit = { onStartEdit(todo) },
                            onDelete = { onRequestDelete(todo) }
                        )
                    }
                }
            }

            // Quick Add Input Box at bottom
            TodoInputBar(
                text = newTodoText,
                onTextChange = { newTodoText = it },
                onAdd = {
                    if (newTodoText.isNotBlank()) {
                        onAddTodo(newTodoText)
                        newTodoText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }

    // Edit Dialog
    uiState.editingTodo?.let { todoToEdit ->
        EditTodoDialog(
            todo = todoToEdit,
            onDismiss = onCancelEdit,
            onConfirm = { newTitle ->
                onSaveEdit(todoToEdit.id, newTitle)
            }
        )
    }

    // Delete Confirmation Dialog
    uiState.todoToDelete?.let { todoToDelete ->
        DeleteConfirmDialog(
            todoTitle = todoToDelete.text,
            onDismiss = onCancelDelete,
            onConfirm = onConfirmDelete
        )
    }

    // Clear Completed Confirmation Dialog
    if (uiState.showClearCompletedDialog) {
        ClearCompletedDialog(
            completedCount = uiState.completedCount,
            onDismiss = onCancelClearCompleted,
            onConfirm = onConfirmClearCompleted
        )
    }
}

@Composable
fun TodoHeader(
    uiState: TodoUiState,
    isSearchExpanded: Boolean,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearCompletedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Todo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (uiState.totalCount == 0) {
                        "No tasks created"
                    } else {
                        "${uiState.completedCount} of ${uiState.totalCount} completed"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleSearch,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("search_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSearchExpanded) Icons.Default.Clear else Icons.Default.Search,
                        contentDescription = if (isSearchExpanded) "Close Search" else "Search Tasks",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.completedCount > 0) {
                    IconButton(
                        onClick = onClearCompletedClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("clear_completed_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Completed Tasks",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Progress bar
        if (uiState.totalCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            val animatedProgress by animateFloatAsState(
                targetValue = uiState.progress,
                label = "progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }

        // Search text field
        AnimatedVisibility(
            visible = isSearchExpanded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search tasks...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun TodoFilterRow(
    currentFilter: TodoFilter,
    totalCount: Int,
    activeCount: Int,
    completedCount: Int,
    onSelectFilter: (TodoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = currentFilter == TodoFilter.ALL,
            onClick = { onSelectFilter(TodoFilter.ALL) },
            label = { Text("All ($totalCount)") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("filter_chip_all")
        )

        FilterChip(
            selected = currentFilter == TodoFilter.ACTIVE,
            onClick = { onSelectFilter(TodoFilter.ACTIVE) },
            label = { Text("Active ($activeCount)") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("filter_chip_active")
        )

        FilterChip(
            selected = currentFilter == TodoFilter.COMPLETED,
            onClick = { onSelectFilter(TodoFilter.COMPLETED) },
            label = { Text("Completed ($completedCount)") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("filter_chip_completed")
        )
    }
}

@Composable
fun TodoCard(
    todo: Todo,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("todo_card_${todo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (todo.isCompleted) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox / Circular toggle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (todo.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .background(
                        if (todo.isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent
                    )
                    .clickable(onClick = onToggle)
                    .testTag("todo_checkbox_${todo.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Todo Text
            Text(
                text = todo.text,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (todo.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onToggle)
                    .testTag("todo_text_${todo.id}"),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Edit Action
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("edit_todo_${todo.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Todo",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Delete Action
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("delete_todo_${todo.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Todo",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TodoInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("What needs to be done?") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("todo_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onAdd,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("add_todo_button"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    }
}

@Composable
fun TodoEmptyState(
    filter: TodoFilter,
    hasAnyTodos: Boolean,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        searchQuery.isNotBlank() -> Icons.Default.Search
                        filter == TodoFilter.COMPLETED -> Icons.Outlined.CheckCircle
                        else -> Icons.Default.PlaylistAddCheck
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val title = when {
                searchQuery.isNotBlank() -> "No matching tasks"
                !hasAnyTodos -> "No tasks yet"
                filter == TodoFilter.ACTIVE -> "All caught up!"
                filter == TodoFilter.COMPLETED -> "No completed tasks"
                else -> "No tasks"
            }

            val subtitle = when {
                searchQuery.isNotBlank() -> "Try searching for a different keyword"
                !hasAnyTodos -> "Type your task below and tap + to get organized"
                filter == TodoFilter.ACTIVE -> "You've finished all your active tasks"
                filter == TodoFilter.COMPLETED -> "Tasks you complete will show up here"
                else -> "Add a task to get started"
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EditTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(todo.text) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Task description") },
                    singleLine = false,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (editedText.isNotBlank()) {
                            onConfirm(editedText)
                        }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("edit_todo_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            ElevatedButton(
                onClick = {
                    if (editedText.isNotBlank()) {
                        onConfirm(editedText)
                    }
                },
                modifier = Modifier.testTag("save_edit_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_edit_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteConfirmDialog(
    todoTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Task?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete \"$todoTitle\"? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            ElevatedButton(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_delete_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ClearCompletedDialog(
    completedCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear Completed Tasks?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "This will remove all $completedCount completed tasks permanently.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            ElevatedButton(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.testTag("confirm_clear_completed_button")
            ) {
                Text("Clear All")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_clear_completed_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
