package me.ykrank.s1next.widget;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import me.ykrank.s1next.util.StringUtil;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * decode like '\u' unicode response string
 */

public final class UniDecoderConverterFactory extends Converter.Factory {
    public static UniDecoderConverterFactory create() {
        return new UniDecoderConverterFactory();
    }

    private UniDecoderConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return UniDecoderResponseBodyConverter.INSTANCE;
        }

        return null;
    }

    private static final class UniDecoderResponseBodyConverter implements Converter<ResponseBody, String> {
        static final UniDecoderResponseBodyConverter INSTANCE = new UniDecoderResponseBodyConverter();

        @Override
        public String convert(ResponseBody value) throws IOException {
            String str = value.string();
            str = StringUtil.uniDecode(str);
            return str;
        }


    }
}
