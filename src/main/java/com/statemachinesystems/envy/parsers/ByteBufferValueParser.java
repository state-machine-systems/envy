package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * {@link ValueParser} implementation for {@link java.nio.ByteBuffer} values.
 * <p>
 * Supports Base64 and URL-safe Base64 encodings.
 */
public class ByteBufferValueParser implements ValueParser<ByteBuffer> {

    @Override
    public ByteBuffer parseValue(String value) {
        Base64.Decoder decoder = value.contains("+") || value.contains("/") ? Base64.getDecoder() : Base64.getUrlDecoder();
        return ByteBuffer.wrap(decoder.decode(value)).asReadOnlyBuffer();
    }

    @Override
    public Class<ByteBuffer> getValueClass() {
        return ByteBuffer.class;
    }
}
