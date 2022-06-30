package com.eidu.content.learningpackages.domain

import kotlinx.serialization.Serializable

@Serializable
data class LearningPackageMeta(
    val app: AppVersion,
    val launchUnitActivity: String
)
