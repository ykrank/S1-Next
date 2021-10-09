package com.github.ykrank.androidtools.widget.track.event

import java.util.*

/**
 * Created by ykrank on 2016/12/27.
 */
open class TrackEvent {

    var group: String? = null

    var name: String? = null

    private val data: MutableMap<String, String?> = hashMapOf()

    /**
     * use to verify handler
     */
    val eventType: Class<*>
        get() = this.javaClass

    protected constructor()

    constructor(group: String?, name: String?, data: Map<String, String?>) {
        this.group = group
        this.name = name
        this.data.putAll(data)
    }

    fun getData(): Map<String, String?> {
        return data
    }

    fun addData(key: String, value: String?) {
        data[key] = value
    }

    fun addData(data: Map<String, String?>) {
        this.data.putAll(data)
    }
}
