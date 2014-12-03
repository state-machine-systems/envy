package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.util.UUID;

/**
 * {@link ValueParser} implementation for {@link java.util.UUID} values.
 */
public class UuidValueParser implements ValueParser<UUID> {

    @Override
    public UUID parseValue(String value) {
        return UUID.fromString(value);
    }

    @Override
    public Class<UUID> getValueClass() {
        return UUID.class;
    }
}
