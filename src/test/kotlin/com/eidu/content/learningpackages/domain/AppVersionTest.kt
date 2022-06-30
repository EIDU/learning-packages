package com.eidu.content.learningpackages.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.eidu.content.learningpackages.testutil.assertSerializationIsStable
import org.junit.jupiter.api.Test

class AppVersionTest {
    @Test
    fun `toString converts to string`() = assertThat(APP_VERSION.toString()).isEqualTo("$APP_ID:$VERSION")

    @Test
    fun `fromString converts from string`() =
        assertThat(AppVersion.fromString("$APP_ID:$VERSION")).isEqualTo(APP_VERSION)

    @Test
    fun `serializes and deserializes`() = assertSerializationIsStable(APP_VERSION)

    companion object {
        private val APP_VERSION = AppVersion("app", "version")
        private val APP_ID = APP_VERSION.appId
        private val VERSION = APP_VERSION.version
    }
}
