package com.eidu.content.learningpackages

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.startsWith
import com.eidu.content.learningpackages.domain.LearningAppVersion
import com.eidu.content.learningpackages.domain.LearningPackageMeta
import com.eidu.content.learningpackages.domain.LearningUnit
import com.eidu.content.learningpackages.domain.LearningUnitList
import com.eidu.content.learningpackages.util.json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class LearningPackageTest {

    private lateinit var tempFile: File
    private lateinit var learningPackage: LearningPackage

    @BeforeEach
    fun before() {
        tempFile = File.createTempFile("learning-package", ".zip")
        javaClass.getResourceAsStream("/learning-package.zip").use { resource ->
            requireNotNull(resource)
            tempFile.outputStream().use { resource.copyTo(it) }
        }
        learningPackage = LearningPackage(tempFile)
    }

    @AfterEach
    fun after() {
        learningPackage.close()
        tempFile.delete()
    }

    @Test
    fun `gets learning package meta`() =
        assertThat(learningPackage.meta).isEqualTo(
            LearningPackageMeta(
                LearningAppVersion("com.eidu.integration.sample.app", "0.0.1"),
                "com.eidu.integration.sample.app.ui.MainActivity"
            )
        )

    @Test
    fun `gets learning unit list`() =
        assertThat(learningPackage.learningUnitList).isEqualTo(
            LearningUnitList(
                listOf(
                    LearningUnit(
                        "unit1",
                        "sample.png",
                        mapOf("category1" to setOf("tag1", "tag2"), "category2" to setOf("tag3")),
                        mapOf("title" to "the title", "description" to "the description", "emptyField" to ""),
                        listOf("subfolder/", "text.txt")
                    ),
                    LearningUnit("unit2"),
                )
            )
        )

    @Test
    fun `gets and reads icons`() {
        assertThat(learningPackage.icons.keys).containsExactlyInAnyOrder("sample.png")

        assertThat(learningPackage.icons["absent"]).isNull()

        assertThat(learningPackage.icons["sample.png"]?.read()?.readBytes()?.sliceArray(0..3))
            .isNotNull().isEqualTo(PNG_SIGNATURE)
        assertThat(learningPackage.icons["sample.png"]?.size).isNotNull().isEqualTo(ICON_SIZE)
    }

    @Test
    fun `gets and reads assets`() {
        assertThat(learningPackage.assets.keys)
            .containsExactlyInAnyOrder("text.txt", "subfolder/image.jpg", "subfolder/audio.mp3")

        assertThat(learningPackage.assets["absent"]).isNull()

        assertThat(learningPackage.assets["text.txt"]?.read()?.readBytes()?.decodeToString())
            .isNotNull().startsWith("This")

        assertThat(learningPackage.assets["text.txt"]?.size).isNotNull().isEqualTo(TEXT_SIZE)

        assertThat(learningPackage.assets["subfolder/image.jpg"]?.read()?.readBytes()?.sliceArray(0..3))
            .isNotNull().isEqualTo(JPEG_SIGNATURE)
        assertThat(learningPackage.assets["subfolder/image.jpg"]?.size).isNotNull().isEqualTo(JPEG_SIZE)

        assertThat(learningPackage.assets["subfolder/audio.mp3"]?.read()?.readBytes()?.sliceArray(0..3))
            .isNotNull().isEqualTo(MP3_SIGNATURE)
        assertThat(learningPackage.assets["subfolder/audio.mp3"]?.size).isNotNull().isEqualTo(MP3_SIZE)
    }

    @Test
    fun `reads units binary`() {
        assertThat(json.decodeFromString<LearningUnitList>(learningPackage.readUnits().readBytes().decodeToString()))
            .isEqualTo(learningPackage.learningUnitList)
    }

    @Test
    fun `reads APK`() {
        assertThat(learningPackage.readApk().readBytes().sliceArray(0..3))
            .isEqualTo(APK_SIGNATURE)
    }

    companion object {
        private val JPEG_SIGNATURE = byteArrayOf(-1, -0x28, -1, -0x20)
        private val MP3_SIGNATURE = byteArrayOf(-1, -0xd, -0x7c, 0x44)
        private val PNG_SIGNATURE = byteArrayOf(-0x77, 0x50, 0x4e, 0x47)
        private val APK_SIGNATURE = byteArrayOf(0x50, 0x4B, 0x03, 0x04)

        private const val ICON_SIZE = 4669L
        private const val TEXT_SIZE = 29L
        private const val MP3_SIZE = 23040L
        private const val JPEG_SIZE = 150321L
    }
}
