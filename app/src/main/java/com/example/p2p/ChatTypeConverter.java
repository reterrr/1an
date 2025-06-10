// ChatTypeConverter.java
package com.example.p2p;

import io.objectbox.converter.PropertyConverter;

public class ChatTypeConverter implements PropertyConverter<ChatType, Integer> {

    @Override
    public ChatType convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        for (ChatType type : ChatType.values()) {
            if (type.value == databaseValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ChatType value: " + databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(ChatType entityProperty) {
        return entityProperty == null ? null : entityProperty.value;
    }
}
