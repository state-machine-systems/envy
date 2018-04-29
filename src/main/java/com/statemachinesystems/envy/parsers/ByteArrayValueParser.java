package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.util.Base64;

/**
 * {@link ValueParser} implementation for {@link byte[]} values.
 * <p>
 * Supports Base64 and URL-safe Base64 encodings.
 */
public class ByteArrayValueParser implements ValueParser<byte[]> {

    @Override
    public byte[] parseValue(String value) {
        Base64.Decoder decoder = value.contains("+") || value.contains("/") ? Base64.getDecoder() : Base64.getUrlDecoder();
        return decoder.decode(value);
    }

    @Override
    public Class<byte[]> getValueClass() {
        return byte[].class;
    }
}
