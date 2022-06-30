package com.eidu.content.learningpackages.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class JsonUtilTest {
    @Serializable
    data class Data(val field: String)

    @Test
    fun `parseJson parses JSON from ByteArray`() {
        assertThat("""{"field":"value"}""".toByteArray().parseJson<Data>()).isEqualTo(Data("value"))
    }
}
