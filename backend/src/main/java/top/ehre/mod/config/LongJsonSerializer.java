package top.ehre.mod.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class LongJsonSerializer extends JsonSerializer<Long> {
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (value != null) {
            jsonGenerator.writeNumber(value);
        }
    }
}
