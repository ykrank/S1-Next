package cl.monsoon.s1next.singleton;

import android.os.RemoteException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.util.ObjectUtil;

public enum MyObjectExtractor {
    INSTANCE;

    private final ObjectMapper objectMapper;

    MyObjectExtractor() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Deserializes JSON or extracts XML string into POJO.
     * <p>
     * Don't close the InputStream.
     *
     * @throws java.io.IOException        if an I/O error occurs
     * @throws android.os.RemoteException if JSON/XML parsing error occurs
     */
    public static <D extends Extractable> D extract(InputStream in, Class<D> toValueType) throws IOException, RemoteException {
        if (toValueType.isAssignableFrom(Quote.class)) {
            return ObjectUtil.uncheckedCast(Quote.fromXmlString(IOUtils.toString(in)));
        } else {
            return readValue(in, toValueType);
        }
    }

    private static <D extends Extractable> D readValue(InputStream in, Class<D> toValueType) throws IOException, RemoteException {
        try {
            return INSTANCE.objectMapper.readValue(in, toValueType);
        } catch (JsonParseException | JsonMappingException e) {
            throw new RemoteException(e.toString());
        }
    }
}
