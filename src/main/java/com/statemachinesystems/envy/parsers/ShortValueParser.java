package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

public class ShortValueParser implements ValueParser<Short> {

    @Override
    public Short parseValue(String value) {
        return Short.parseShort(value);
    }

    @Override
    public Class<Short> getValueClass() {
        return Short.class;
    }
}
