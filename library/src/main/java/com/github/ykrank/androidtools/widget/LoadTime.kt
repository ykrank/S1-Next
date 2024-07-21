package com.github.ykrank.androidtools.widget

class LoadTime() {
    private val start = System.currentTimeMillis()
    private val timeMap = mutableMapOf<String, Long>()

    val times: Map<String, Long>
        get() = timeMap.mapValues {
            it.value - start
        }

    fun addPoint(name: String) {
        timeMap[name] = System.currentTimeMillis()
    }

    fun start(name: String) {
        timeMap[name + "_start"] = System.currentTimeMillis()
    }

    fun end(name: String) {
        timeMap[name + "_end"] = System.currentTimeMillis()
    }

    inline fun <T> run(name: String, block: () -> T): T {
        start(name)
        return try {
            block()
        } catch (e: Throwable) {
            throw e
        } finally {
            end(name)
        }
    }
}