package cl.monsoon.s1next.singleton;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.util.ServerException;

public enum ObjectExtractor {
    INSTANCE;

    private final ObjectMapper mObjectMapper;

    ObjectExtractor() {
        mObjectMapper = new ObjectMapper();
    }

    /**
     * Deserializes JSON or extracts XML string into POJO.
     * <p>
     * Don't close the InputStream.
     *
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    public static <D extends Extractable> D extract(InputStream in, Class<D> toValueType) throws IOException {
        if (toValueType.isAssignableFrom(Quote.class)) {
            return (D) parseQuote(in);
        } else {
            return readValue(in, toValueType);
        }
    }

    private static Quote parseQuote(InputStream in) throws IOException {
        try {
            return Quote.fromXmlString(CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8)));
        } catch (NullPointerException e) {
            throw new ServerException(e);
        }
    }

    private static <D extends Extractable> D readValue(InputStream in, Class<D> toValueType) throws IOException {
        try {
            return INSTANCE.mObjectMapper.readValue(in, toValueType);
        } catch (JsonParseException | JsonMappingException e) {
            throw new ServerException(e.toString());
        }
    }
}
