package cl.monsoon.s1next.widget;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * Forked from https://github.com/square/retrofit/blob/master/retrofit/src/test/java/retrofit/ToStringConverterFactory.java
 */
public final class ToStringConverterFactory implements Converter.Factory {

    @Override
    public Converter<?> get(Type type) {
        if (type != String.class) {
            return null;
        }

        return new Converter<String>() {

            @Override
            public String fromBody(ResponseBody body) throws IOException {
                return body.string();
            }

            @Override
            public RequestBody toBody(String value) {
                return RequestBody.create(MediaType.parse("text/plain"), value);
            }
        };
    }
}
