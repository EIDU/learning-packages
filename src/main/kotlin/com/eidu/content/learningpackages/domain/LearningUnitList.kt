package com.eidu.content.learningpackages.domain

import kotlinx.serialization.Serializable

@Serializable
data class LearningUnitList(
    val units: List<LearningUnit>
)
