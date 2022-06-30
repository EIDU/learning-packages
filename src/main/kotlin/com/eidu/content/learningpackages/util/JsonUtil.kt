package com.eidu.content.learningpackages.util

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> ByteArray.parseJson() = json.decodeFromString(T::class.serializer(), decodeToString())
