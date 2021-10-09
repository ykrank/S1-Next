package com.github.ykrank.androidtools.data

interface TrackUser {

    val uid: String?

    val name: String?

    val permission: Int

    val extras: Map<String, String>?
}
