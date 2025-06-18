package com.example.pontual.api.models

import com.google.gson.*
import java.lang.reflect.Type

class DeliveryStatusAdapter : JsonDeserializer<DeliveryStatus>, JsonSerializer<DeliveryStatus> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DeliveryStatus {
        val value = json.asString
        return DeliveryStatus.values().find { it.value == value } ?: DeliveryStatus.PENDING
    }

    override fun serialize(src: DeliveryStatus, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value)
    }
} 