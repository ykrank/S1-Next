package com.github.ykrank.androidtools.ui.adapter

/**
 * One model has stable id
 */
interface StableIdModel {

    val stableId: Long
        get() {
            return hashCode().toLong()
        }

    companion object {
        const val ID_PROGRESS = -11L
        const val ID_PROGRESS_FOOTER = -12L
        const val ID_FOOTER = -13L
    }
}