package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

public class StringValueParser implements ValueParser<String> {

    @Override
    public String parseValue(String value) {
        return value;
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }
}
