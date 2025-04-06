package by.farad.accesscontrolserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectMapperSingleton() {}

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
