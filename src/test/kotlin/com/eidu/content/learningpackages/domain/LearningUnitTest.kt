package com.eidu.content.learningpackages.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.eidu.content.learningpackages.testutil.assertSerializationIsStable
import com.eidu.content.learningpackages.util.json
import org.junit.jupiter.api.Test

class LearningUnitTest {
    @Test
    fun `gets title`() {
        assertThat(UNIT.title).isEqualTo("the title")
        assertThat(MINIMAL_UNIT.title).isNull()
    }

    @Test
    fun `gets description`() {
        assertThat(UNIT.description).isEqualTo("the description")
        assertThat(MINIMAL_UNIT.description).isNull()
    }

    @Test
    fun `determines whether unit may access an asset`() {
        assertThat(UNIT.mayAccessAsset("text.txt")).isTrue()
        assertThat(UNIT.mayAccessAsset("other_file.txt")).isFalse()
        assertThat(UNIT.mayAccessAsset("subfolder/some_file.txt")).isTrue()
        assertThat(UNIT.mayAccessAsset("subfolder/subsubfolder/some_file.txt")).isTrue()
        assertThat(UNIT.mayAccessAsset("otherfolder/text.txt")).isFalse()
    }

    @Test
    fun `serializes and deserializes`() {
        assertSerializationIsStable(UNIT)
        assertSerializationIsStable(MINIMAL_UNIT)
    }

    @Test
    fun `deserialization omits empty tag categories`() = assertThat(
        json.decodeFromString(
            LearningUnit.serializer(),
            """
               {
                 "id": "unit2",
                 "tags": {
                   "emptyCategory": []
                 }
               } 
            """
        )
    ).isEqualTo(MINIMAL_UNIT)

    @Test
    fun `merges with other unit`() = assertThat(UNIT.merge(OTHER_UNIT)).isEqualTo(
        LearningUnit(
            "unit1",
            "sample.png",
            mapOf(
                "category1" to setOf("tag1", "tag2", "tag3"),
                "category2" to setOf("tag3"),
                "category3" to setOf("tag4")
            ),
            mapOf(
                "title" to "the title",
                "description" to "the description",
                "emptyfield" to "",
                "new-field" to "new value"
            ),
            listOf("subfolder/", "text.txt", "new-folder/")
        )
    )

    @Test
    fun `merges minimal unit with other unit`() = assertThat(MINIMAL_UNIT.merge(OTHER_UNIT)).isEqualTo(
        LearningUnit(
            "unit2",
            "sample3.png",
            mapOf("category1" to setOf("tag2", "tag3"), "category3" to setOf("tag4")),
            mapOf(
                "title" to "the title 2",
                "new-field" to "new value"
            ),
            listOf("new-folder/")
        )
    )

    @Test
    fun `merges unit with itself`() = assertThat(UNIT.merge(UNIT)).isEqualTo(UNIT)

    @Test
    fun `merges minimal unit with itself`() = assertThat(MINIMAL_UNIT.merge(MINIMAL_UNIT)).isEqualTo(MINIMAL_UNIT)

    companion object {
        private val UNIT = LearningUnit(
            "unit1",
            "sample.png",
            mapOf("category1" to setOf("tag1", "tag2"), "category2" to setOf("tag3")),
            mapOf("title" to "the title", "description" to "the description", "emptyfield" to ""),
            listOf("subfolder/", "text.txt")
        )

        private val MINIMAL_UNIT = LearningUnit("unit2")

        private val OTHER_UNIT = LearningUnit(
            "unit3",
            "sample3.png",
            mapOf("category1" to setOf("tag2", "tag3"), "category3" to setOf("tag4")),
            mapOf("title" to "the title 2", "new-field" to "new value"),
            listOf("new-folder/")
        )
    }
}
