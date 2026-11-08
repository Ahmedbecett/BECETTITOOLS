package com.becetti.tools.core.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.lang.reflect.Type

/**
 * Durable, dependency-light persistence for the new modules.
 *
 * Each store owns one JSON document inside the app's private `filesDir`, exposes it as an observable
 * [StateFlow] and writes synchronously on mutation (documents are small — kilobytes). This keeps
 * projects, customers, invoices and activity available offline with zero migrations and no schema
 * risk, and is trivially inspectable/exportable by the user.
 */
class LocalStore<T>(
    context: Context,
    private val fileName: String,
    private val type: Type,
    private val empty: () -> T
) {
    private val appContext = context.applicationContext
    private val gson = Gson()

    private val file: File
        get() = File(appContext.filesDir, "$fileName.json")

    private val _data = MutableStateFlow(read())
    val data: StateFlow<T> = _data

    val current: T get() = _data.value

    private fun read(): T = runCatching {
        val f = file
        if (!f.exists()) empty() else gson.fromJson<T>(f.readText(Charsets.UTF_8), type) ?: empty()
    }.getOrDefault(empty())

    fun set(value: T) {
        _data.value = value
        runCatching { file.writeText(gson.toJson(value, type), Charsets.UTF_8) }
    }

    fun mutate(block: (T) -> T) = set(block(_data.value))

    fun delete() {
        _data.value = empty()
        runCatching { file.delete() }
    }

    companion object {
        fun listType(clazz: Class<*>): Type = TypeToken.getParameterized(List::class.java, clazz).type
    }
}
