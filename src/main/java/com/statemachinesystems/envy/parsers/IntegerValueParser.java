package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

public class IntegerValueParser implements ValueParser<Integer> {

    @Override
    public Integer parseValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

}
