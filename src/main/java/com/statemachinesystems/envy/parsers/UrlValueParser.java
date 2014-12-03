package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link ValueParser} implementation for {@link java.net.URL} values.
 */
public class UrlValueParser implements ValueParser<URL> {

    @Override
    public URL parseValue(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Class<URL> getValueClass() {
        return URL.class;
    }
}
