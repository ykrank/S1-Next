package me.ykrank.s1next.widget

import android.content.Context
import android.util.SparseArray

import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Emoticon
import java.text.DecimalFormat

/**
 * A factory provides emotions.
 */
class EmoticonFactory(context: Context) {

    val emotionTypeTitles: List<String> =
        context.resources.getStringArray(R.array.emoticon_type).toList()
    private val mEmoticons: SparseArray<List<Emoticon>> = SparseArray()

    private val animalEmoticonList: List<Emoticon> by lazy {
        createEmotionList(30, "animal2017", "a")
    }

    private val bundamEmoticonList: List<Emoticon> by lazy {
        createEmotionList(37, "bundam2017", "b")
    }

    private val cartonEmoticonList: List<Emoticon> by lazy {
        createEmotionList(430, "carton2017", "c")
    }

    private val deviceEmoticonList: List<Emoticon> by lazy {
        createEmotionList(44, "device2017", "d")
    }

    private val faceEmoticonList: List<Emoticon> by lazy {
        createEmotionList(275, "face2017", "f")
    }

    private val gooseEmoticonList: List<Emoticon> by lazy {
        createEmotionList(74, "goose2017", "g")
    }

    fun getEmoticonsByIndex(index: Int): List<Emoticon> {
        var emoticons: List<Emoticon>? = mEmoticons.get(index)
        if (emoticons == null) {
            emoticons = when (index) {
                0 -> faceEmoticonList
                1 -> cartonEmoticonList
                2 -> animalEmoticonList
                3 -> deviceEmoticonList
                4 -> gooseEmoticonList
                5 -> bundamEmoticonList
                else -> throw IllegalStateException("Unknown emoticon index: $index.")
            }
        }
        mEmoticons.put(index, emoticons)

        return emoticons
    }

    private fun check(){

    }

    companion object {
        val FORMAT_LEAD_ZERO_3 = DecimalFormat("000")
        const val ASSET_PATH_EMOTICON = "file:///android_asset/image/emoticon/"

        private fun emoticon(emoticonFileName: String, emoticonEntity: String): Emoticon {
            return Emoticon(ASSET_PATH_EMOTICON + emoticonFileName, emoticonEntity)
        }

        private fun createEmotionList(
            size: Int,
            fileDir: String,
            entityDir: String
        ): MutableList<Emoticon> {
            val result = mutableListOf<Emoticon>()
            for (i in 1..size) {
                val code = FORMAT_LEAD_ZERO_3.format(i)
                result.add(emoticon("${fileDir}/${code}.", "[${entityDir}:${code}]"))
            }
            return result
        }
    }
}
