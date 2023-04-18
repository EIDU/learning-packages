package com.eidu.content.learningpackages.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TagsSerializer : KSerializer<Map<String, Set<String>>> {
    private val surrogateSerializer = MapSerializer(String.serializer(), SetSerializer(String.serializer()))

    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<String, Set<String>>) =
        encoder.encodeSerializableValue(surrogateSerializer, value)

    override fun deserialize(decoder: Decoder): Map<String, Set<String>> =
        decoder.decodeSerializableValue(surrogateSerializer).filterValues { it.isNotEmpty() }
}
