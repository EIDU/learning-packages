package com.eidu.content.learningpackages.domain

import com.eidu.content.learningpackages.testutil.assertSerializationIsStable
import org.junit.jupiter.api.Test

class LearningPackageMetaTest {
    @Test
    fun `serializes and deserializes`() = assertSerializationIsStable(META)

    companion object {
        private val META = LearningPackageMeta(
            LearningAppVersion("app", "version"),
            "activity"
        )
    }
}
