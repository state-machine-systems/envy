package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.util.regex.Pattern;

/**
 * {@link ValueParser} implementation for {@link java.util.regex.Pattern} values.
 */
public class PatternValueParser implements ValueParser<Pattern> {

    @Override
    public Pattern parseValue(String value) {
        return Pattern.compile(value);
    }

    @Override
    public Class<Pattern> getValueClass() {
        return Pattern.class;
    }
}
