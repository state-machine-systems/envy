package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.URI;
import java.net.URISyntaxException;

public class UriValueParser implements ValueParser<URI> {

    @Override
    public URI parseValue(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Class<URI> getValueClass() {
        return URI.class;
    }
}
