package com.eidu.content.learningpackages.testutil

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.eidu.content.learningpackages.util.json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

inline fun <reified T> assertSerializationIsStable(obj: T) =
    assertThat(json.decodeFromJsonElement<T>(json.encodeToJsonElement(obj))).isEqualTo(obj)
