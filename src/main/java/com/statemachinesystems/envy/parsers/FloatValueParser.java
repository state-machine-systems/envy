package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

public class FloatValueParser implements ValueParser<Float> {

    @Override
    public Float parseValue(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public Class<Float> getValueClass() {
        return Float.class;
    }
}
