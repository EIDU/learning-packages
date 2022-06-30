package com.eidu.content.learningpackages.domain

import kotlinx.serialization.Serializable

@Serializable
data class AppVersion(
    val appId: String,
    val version: String
) {
    override fun toString(): String = "$appId:$version"

    companion object {
        fun fromString(string: String): AppVersion {
            val parts = string.split(':', limit = 2)
            return AppVersion(parts[0], parts[1])
        }
    }
}
