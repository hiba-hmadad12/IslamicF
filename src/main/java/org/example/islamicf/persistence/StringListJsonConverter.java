package org.example.islamicf.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try { return attribute == null ? null : MAPPER.writeValueAsString(attribute); }
        catch (Exception e) { throw new IllegalArgumentException("Cannot serialize flags list", e); }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try { return (dbData == null || dbData.isBlank()) ? null : MAPPER.readValue(dbData, TYPE); }
        catch (Exception e) { throw new IllegalArgumentException("Cannot deserialize flags list", e); }
    }
}
