package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Todo
import com.example.storage.TodoLocalStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Todo", appName)
  }

  @Test
  fun `local storage save and load todos`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = TodoLocalStorage(context)

    val todos = listOf(
      Todo(text = "Buy groceries", isCompleted = false),
      Todo(text = "Finish Android project", isCompleted = true)
    )

    storage.saveTodos(todos)
    val loaded = storage.loadTodos()

    assertEquals(2, loaded.size)
    assertEquals("Buy groceries", loaded[0].text)
    assertEquals(false, loaded[0].isCompleted)
    assertEquals("Finish Android project", loaded[1].text)
    assertEquals(true, loaded[1].isCompleted)
  }
}

