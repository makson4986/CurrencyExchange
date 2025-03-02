package org.makson.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {
    private static final JsonMapper INSTANCE = new JsonMapper();

    private JsonMapper() {
    }

    public String dtoToJson(Object dto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonMapper getInstance() {
        return INSTANCE;
    }
}
