package com.example.p2p;

import com.example.p2p.Model.State;

import io.objectbox.converter.PropertyConverter;

public class MessageStateConverter implements PropertyConverter<State, Integer> {
    @Override
    public State convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        for (State type : State.values()) {
            if (type.value == databaseValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ChatType value: " + databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(State entityProperty) {
        return entityProperty == null ? null : entityProperty.value;
    }
}
