package org.example.islamicf.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.util.Map;

@Converter
public class MapStringBigDecimalJsonConverter implements AttributeConverter<Map<String, BigDecimal>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, BigDecimal>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, BigDecimal> attribute) {
        try { return attribute == null ? null : MAPPER.writeValueAsString(attribute); }
        catch (Exception e) { throw new IllegalArgumentException("Cannot serialize ratios map", e); }
    }

    @Override
    public Map<String, BigDecimal> convertToEntityAttribute(String dbData) {
        try { return (dbData == null || dbData.isBlank()) ? null : MAPPER.readValue(dbData, TYPE); }
        catch (Exception e) { throw new IllegalArgumentException("Cannot deserialize ratios map", e); }
    }
}
