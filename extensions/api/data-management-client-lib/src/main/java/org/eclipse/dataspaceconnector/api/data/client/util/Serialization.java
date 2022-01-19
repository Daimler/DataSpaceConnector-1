package org.eclipse.dataspaceconnector.api.data.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.SerializationException;

import java.io.IOException;
import java.io.InputStream;

public class Serialization {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String serialize(Object object) throws SerializationException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }

    public static <T> T deserialize(InputStream inputStream, Class<?> targetClass) throws SerializationException {
        try {
            //noinspection unchecked
            return (T) OBJECT_MAPPER.readValue(inputStream, targetClass);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static <T> T deserialize(InputStream inputStream, TypeReference<?> targetTypeReference) throws SerializationException {
        try {
            //noinspection unchecked
            return (T) OBJECT_MAPPER.readValue(inputStream, targetTypeReference);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
