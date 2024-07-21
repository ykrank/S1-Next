package com.github.ykrank.androidtools.widget.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class JsonBooleanDeserializer : JsonDeserializer<Boolean>() {
    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): Boolean {
        return p0?.text == "1"
    }
}

class JsonBooleanSerializer : JsonSerializer<Boolean>() {
    override fun serialize(p0: Boolean?, p1: JsonGenerator?, p2: SerializerProvider?) {
        if (p0 != null) {
            p1?.writeString(if (p0) "1" else "0")
        }
    }

}