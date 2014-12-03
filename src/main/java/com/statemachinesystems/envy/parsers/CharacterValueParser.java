package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Character} values.
 */
public class CharacterValueParser implements ValueParser<Character> {

    @Override
    public Character parseValue(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Can't parse character from empty value");
        }
        return value.charAt(0);
    }

    @Override
    public Class<Character> getValueClass() {
        return Character.class;
    }
}
