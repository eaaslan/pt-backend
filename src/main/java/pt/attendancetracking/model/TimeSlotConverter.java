package pt.attendancetracking.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;

@Converter
public class TimeSlotConverter implements AttributeConverter<LocalDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(LocalDateTime attribute) {
        if (attribute == null) return null;
        return attribute.withMinute(0).withSecond(0).withNano(0);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(LocalDateTime dbData) {
        if (dbData == null) return null;
        return dbData.withMinute(0).withSecond(0).withNano(0);
    }
}