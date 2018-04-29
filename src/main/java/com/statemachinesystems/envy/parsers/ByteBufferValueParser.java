package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.nio.ByteBuffer;

/**
 * {@link ValueParser} implementation for {@link java.nio.ByteBuffer} values.
 * <p>
 * Supports Base64 and URL-safe Base64 encodings.
 */
public class ByteBufferValueParser implements ValueParser<ByteBuffer> {

    @Override
    public ByteBuffer parseValue(String value) {
        return ByteBuffer.wrap(new ByteArrayValueParser().parseValue(value));
    }

    @Override
    public Class<ByteBuffer> getValueClass() {
        return ByteBuffer.class;
    }
}
