package me.ykrank.s1next.widget

import com.github.ykrank.androidtools.util.StringUtils
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * decode like `\ u` unicode response string.
 * and fix illegal json error
 */

class RawJsonConverterFactory private constructor() : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if (type === String::class.java) {
            return RawJsonResponseBodyConverter
        }

        return null
    }

    private object RawJsonResponseBodyConverter : Converter<ResponseBody, String> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String {
            var str = value.string()
            //decode like `\ u` unicode response string
            str = StringUtils.uniDecode(str)
            //replace like `A:,` with `A:null,`
            str = str.replace(":,", ":null,")
            return str
        }
    }

    companion object {
        fun create(): RawJsonConverterFactory {
            return RawJsonConverterFactory()
        }
    }
}
