package com.example.storage

import android.content.Context
import com.example.model.Todo
import org.json.JSONArray
import org.json.JSONObject

class TodoLocalStorage(context: Context) {
    private val prefs = context.getSharedPreferences("todo_local_storage", Context.MODE_PRIVATE)
    private val key = "todos_json_list"

    fun loadTodos(): List<Todo> {
        val jsonString = prefs.getString(key, null) ?: return emptyList()
        val list = mutableListOf<Todo>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Todo(
                        id = obj.optString("id"),
                        text = obj.optString("text"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveTodos(todos: List<Todo>) {
        try {
            val jsonArray = JSONArray()
            for (todo in todos) {
                val obj = JSONObject()
                obj.put("id", todo.id)
                obj.put("text", todo.text)
                obj.put("isCompleted", todo.isCompleted)
                obj.put("createdAt", todo.createdAt)
                jsonArray.put(obj)
            }
            prefs.edit().putString(key, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
