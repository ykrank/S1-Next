package cl.monsoon.s1next.singleton;

import android.os.RemoteException;

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

    private MyObjectExtractor() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Deserialize JSON or extract XML string into object.
     */
    public static <D extends Extractable> D readValue(InputStream in, Class<D> toValueType) throws RemoteException {
        try {
            if (toValueType.isAssignableFrom(Quote.class)) {
                return ObjectUtil.uncheckedCast(Quote.fromXmlString(IOUtils.toString(in)));
            } else {
                return INSTANCE.objectMapper.readValue(in, toValueType);
            }
        } catch (IOException e) {
            throw new RemoteException(e.toString());
        }
    }
}
