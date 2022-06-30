package com.eidu.content.learningpackages.domain

import kotlinx.serialization.Serializable

@Serializable
data class LearningPackageMeta(
    val app: LearningAppVersion,
    val launchUnitActivity: String
)
