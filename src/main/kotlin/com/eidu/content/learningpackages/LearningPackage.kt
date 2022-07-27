package com.eidu.content.learningpackages

import com.eidu.content.learningpackages.domain.LearningAppVersion
import com.eidu.content.learningpackages.domain.LearningPackageMeta
import com.eidu.content.learningpackages.domain.LearningUnitList
import com.eidu.content.learningpackages.util.getStrings
import com.eidu.content.learningpackages.util.json
import com.eidu.content.learningpackages.util.parseXml
import kotlinx.serialization.decodeFromString
import net.dongliu.apk.parser.ApkFile
import org.w3c.dom.Document
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class LearningPackage(
    file: File
) : Closeable {

    private val zipFile = ZipFile(file)

    private val entries: Map<String, ZipEntry> by lazy {
        zipFile.entries().asSequence().filter {
            !it.isDirectory && !it.name.contains(".DS_Store") && !it.name.contains("__MACOSX")
        }.associateBy { it.name }
    }

    val meta: LearningPackageMeta by lazy {
        val tempFile = File.createTempFile("app", ".apk")
        try {
            extractApk(tempFile)
            parseApk(tempFile)
        } finally {
            tempFile.delete()
        }
    }

    val learningUnitList: LearningUnitList by lazy {
        json.decodeFromString(readUnits().reader().use { it.readText() })
    }

    val assets: List<String> by lazy {
        entries.keys.mapNotNull { it.removePrefixOrNull(ASSETS_PATH) }
    }

    val icons: List<String> by lazy {
        entries.keys.mapNotNull { it.removePrefixOrNull(ICONS_PATH) }
    }

    fun readApk(): InputStream = zipFile.getInputStream(entries.getValue(APK_PATH))
    fun readUnits(): InputStream = zipFile.getInputStream(entries.getValue(UNITS_PATH))
    fun readAsset(asset: String): InputStream? = entries["$ASSETS_PATH$asset"]?.let { zipFile.getInputStream(it) }
    fun readIcon(icon: String): InputStream? = entries["$ICONS_PATH$icon"]?.let { zipFile.getInputStream(it) }

    private fun extractApk(tempFile: File) {
        readApk().use { apk -> tempFile.outputStream().use { apk.copyTo(it) } }
    }

    private fun parseApk(apk: File) = parseApkManifest(parseXml(ApkFile(apk).manifestXml))

    private fun parseApkManifest(manifest: Document): LearningPackageMeta {
        val packageName = manifest.getStrings("/manifest/@package").single()
        val versionName = manifest.getStrings("/manifest/@versionName").single()
        val launchUnitActivity = manifest.getStrings(
            "/manifest/application/activity" +
                "[intent-filter/action/@name='com.eidu.integration.LAUNCH_LEARNING_UNIT']/@name"
        ).singleOrNull() ?: error(
            "Did not find exactly one activity with an intent filter for com.eidu.integration.LAUNCH_LEARNING_UNIT"
        )

        return LearningPackageMeta(LearningAppVersion(packageName, versionName), launchUnitActivity)
    }

    override fun close() = zipFile.close()

    companion object {
        private const val ASSETS_PATH = "assets/"
        private const val ICONS_PATH = "icons/"
        private const val APK_PATH = "app.apk"
        private const val UNITS_PATH = "units.json"

        private fun String.removePrefixOrNull(prefix: String) =
            if (startsWith(prefix)) substring(prefix.length) else null
    }
}
