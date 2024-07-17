package me.ykrank.s1next.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.SingleTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App

object JsonUtil {

    fun <D> jsonSingleTransformer(dClass: Class<D>): SingleTransformer<String, D> {
        return SingleTransformer { upstream -> upstream.map { App.preAppComponent.jsonMapper.readValue<D>(it, dClass) } }
    }

    fun <T> readJsonNode(mapper: ObjectMapper, jsonNode: JsonNode, javaType: JavaType): T {
        return mapper.readValue(mapper.treeAsTokens(jsonNode), javaType)
    }

    fun <T> readJsonNode(mapper: ObjectMapper, jsonNode: JsonNode, javaType: Class<T>): T {
        return mapper.readValue(mapper.treeAsTokens(jsonNode), javaType)
    }

    fun <T> readJsonNode(mapper: ObjectMapper, jsonNode: JsonNode, javaType: TypeReference<T>): T {
        return mapper.readValue(mapper.treeAsTokens(jsonNode), javaType)
    }
}

suspend fun <T> String.toJson(cls: Class<T>): T {
    return withContext(Dispatchers.IO) {
        App.preAppComponent.jsonMapper.readValue(this@toJson, cls)
    }
}