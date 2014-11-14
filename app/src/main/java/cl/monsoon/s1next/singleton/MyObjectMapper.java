package cl.monsoon.s1next.singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * ObjectMapper singleton.
 */
public enum MyObjectMapper {
    INSTANCE;

    private final ObjectMapper objectMapper;

    private MyObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Deserialize JSON into object.
     */
    public static <D> D readValue(InputStream in, Class<D> toValueType) throws IOException {
        return INSTANCE.objectMapper.readValue(in, toValueType);
    }
}
