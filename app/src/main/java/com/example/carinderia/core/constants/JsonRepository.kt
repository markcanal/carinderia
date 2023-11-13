package com.example.carinderia.core.constants

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonRepository @Inject constructor() {
    @PublishedApi
    internal val format = Json {
        useAlternativeNames = false
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = true
    }

    inline fun <reified T> decodeFromString(string: String): T {
        Timber.tag(TAG).d("Decoding JSON string %s", string)
        return format.decodeFromString(string)
    }

    inline fun <reified T> decodeFromStringArray(strings: Array<String>): List<T> {
        return strings.map { decodeFromString(it) }
    }

    inline fun <reified T> encodeToStringArray(items: Iterable<T>): Array<String> {
        return items.map { encodeToString(it) }.toTypedArray()
    }

    inline fun <reified T> encodeToString(data: T): String {
        Timber.tag(TAG).d("Encoding %s to JSON string", data)
        return format.encodeToString(data)
    }

    @PublishedApi
    internal companion object {
        const val TAG = "JsonRepository"
    }
}