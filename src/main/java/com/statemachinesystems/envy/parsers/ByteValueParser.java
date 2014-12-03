package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Byte} values.
 */
public class ByteValueParser implements ValueParser<Byte> {

    @Override
    public Byte parseValue(String value) {
        // TODO consider treating byte values as unsigned, or hex
        return Byte.parseByte(value);
    }

    @Override
    public Class<Byte> getValueClass() {
        return Byte.class;
    }
}
