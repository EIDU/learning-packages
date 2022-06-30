package com.eidu.content.learningpackages.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

object TagsSerializer : KSerializer<Map<String, Set<String>>> {
    private val surrogateSerializer = serializer<Map<String, Set<String>>>()
    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<String, Set<String>>) =
        encoder.encodeSerializableValue(surrogateSerializer, value)

    override fun deserialize(decoder: Decoder): Map<String, Set<String>> =
        decoder.decodeSerializableValue(surrogateSerializer).filterValues { it.isNotEmpty() }
}
