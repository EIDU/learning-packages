package com.eidu.content.learningpackages.domain

import com.eidu.content.learningpackages.testutil.assertSerializationIsStable
import org.junit.jupiter.api.Test

class LearningUnitListTest {
    @Test
    fun `serializes and deserializes`() = assertSerializationIsStable(LIST)

    companion object {
        private val LIST = LearningUnitList(
            listOf(
                LearningUnit(
                    "unit1",
                    "sample.png",
                    mapOf("category1" to setOf("tag1", "tag2"), "category2" to setOf("tag3")),
                    mapOf("title" to "the title", "description" to "the description", "emptyfield" to ""),
                    listOf("subfolder/", "text.txt")
                ),
                LearningUnit("unit2"),
            )
        )
    }
}
