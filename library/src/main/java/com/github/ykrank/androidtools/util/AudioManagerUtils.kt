package com.github.ykrank.androidtools.util

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager


object AudioManagerUtils {

    /**
     * 判断是否在播放音乐或者视频
     * @param context
     * @return
     */
    fun isMusicOrVideoPlay(context: Context): Boolean {
        return (context.getSystemService(AUDIO_SERVICE) as AudioManager).isMusicActive
    }
}