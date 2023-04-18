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

    /**
     * Merges the other LearningUnit into this one and returns the result. Present values take precedence over absent
     * values, and values present in this instance take precedence over values present in [other].
     */
    fun merge(other: LearningUnit) = LearningUnit(
        id = id,
        icon = icon ?: other.icon,
        tags = mergeTags(tags, other.tags),
        fields = other.fields + fields,
        assets = (assets + other.assets).distinct()
    )

    companion object {
        const val TITLE_FIELD = "title"
        const val DESCRIPTION_FIELD = "description"

        private fun mergeTags(
            first: Map<String, Set<String>>,
            second: Map<String, Set<String>>
        ): Map<String, Set<String>> {
            val result = mutableMapOf<String, MutableSet<String>>()
            first.forEach { (key, value) -> result[key] = value.toMutableSet() }
            second.forEach { (key, value) -> result.getOrPut(key) { mutableSetOf() }.addAll(value) }
            return result
        }
    }
}
