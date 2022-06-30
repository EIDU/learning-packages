package com.eidu.content.learningpackages.domain

import com.eidu.content.learningpackages.util.TagsSerializer
import kotlinx.serialization.Serializable

@Serializable
data class LearningUnit(
    val id: String,
    val icon: String? = null,
    @Serializable(with = TagsSerializer::class)
    val tags: Map<String, Set<String>> = emptyMap(),
    val fields: Map<String, String> = emptyMap(),
    val assets: List<String> = emptyList()
) {
    val title: String? get() = fields[TITLE_FIELD]
    val description: String? get() = fields[DESCRIPTION_FIELD]

    fun mayAccessAsset(filePath: String): Boolean =
        assets.any {
            filePath == it || (it.endsWith('/') && filePath.startsWith(it))
        }

    companion object {
        const val TITLE_FIELD = "title"
        const val DESCRIPTION_FIELD = "description"
    }
}
