package com.eidu.content.learningpackages

import com.eidu.content.learningpackages.domain.LearningUnitList
import com.eidu.content.learningpackages.util.json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LearningPackageWriter {
    fun write(
        out: File,
        apk: File,
        units: LearningUnitList,
        icons: Iterable<File>,
        assetsDirectory: File? = null
    ) {
        try {
            ZipOutputStream(FileOutputStream(out)).use { zip ->
                zip.putEntry("app.apk", apk.inputStream())
                zip.putEntry("units.json", json.encodeToString(units).toByteArray().inputStream())
                icons.forEach { zip.putEntry("icons/${it.name}", it.inputStream()) }

                assetsDirectory?.apply {
                    walk().filter { it.isFile }
                        .forEach { zip.putEntry("assets/${it.toRelativeString(assetsDirectory)}", it.inputStream()) }
                }
            }
        } catch (e: Exception) {
            out.delete()
            throw e
        }
    }

    private fun ZipOutputStream.putEntry(path: String, contents: InputStream) {
        putNextEntry(ZipEntry(path))
        contents.use { it.copyTo(this) }
    }
}
