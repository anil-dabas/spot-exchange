package com.spot.marketdata.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = DepthEntrySerializer.class)
public class DepthEntry {
    private String price;
    private String quantity;
}

class DepthEntrySerializer extends JsonSerializer<DepthEntry> {
    @Override
    public void serialize(DepthEntry value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        gen.writeString(value.getPrice());
        gen.writeString(value.getQuantity());
        gen.writeEndArray();
    }
}
