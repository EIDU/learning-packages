package com.eidu.content.learningpackages

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.eidu.content.learningpackages.domain.LearningUnit
import com.eidu.content.learningpackages.domain.LearningUnitList
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

class LearningPackageWriterTest {
    private val writer = LearningPackageWriter()

    private val tempDir = createTempDirectory().toFile()

    @Test
    fun `writes learning package that is readable by LearningPackage`() {
        val packageFile = createTempFile().toFile()
        val apk = fileFromResource("app.apk")
        val icon = fileFromResource("sample.png")
        val assetsDirectory = tempDir.resolve("assets").apply { mkdir() }
        val asset = assetsDirectory.resolve("text.txt").apply { writeFromResource("text.txt") }

        writer.write(packageFile, apk, UNITS, listOf(icon), assetsDirectory)

        val learningPackage = LearningPackage(packageFile)
        assertThat(learningPackage.readApk().readBytes()).isEqualTo(apk.readBytes())
        assertThat(learningPackage.learningUnitList).isEqualTo(UNITS)
        assertThat(learningPackage.icons.keys).containsOnly("sample.png")
        assertThat(learningPackage.icons["sample.png"]?.read()?.readBytes() contentEquals icon.readBytes()).isTrue()
        assertThat(learningPackage.assets.keys).containsOnly("text.txt")
        assertThat(learningPackage.assets["text.txt"]?.read()?.readBytes() contentEquals asset.readBytes()).isTrue()
    }

    private fun fileFromResource(resourcePath: String) =
        tempDir.resolve(resourcePath).apply { writeFromResource(resourcePath) }

    companion object {
        private val UNITS = LearningUnitList(
            listOf(
                LearningUnit(
                    "unit1",
                    "sample.png",
                    mapOf("category1" to setOf("tag1", "tag2"), "category2" to setOf("tag3")),
                    mapOf("title" to "the title", "description" to "the description", "emptyfield" to ""),
                    listOf("text.txt")
                ),
                LearningUnit("unit2"),
            )
        )

        private fun File.writeFromResource(resourcePath: String) =
            requireNotNull(
                LearningPackageWriterTest::class.java.getResourceAsStream("/$resourcePath")
            ) { "Resource not found: $resourcePath" }
                .use { it.copyTo(outputStream()) }
    }
}
