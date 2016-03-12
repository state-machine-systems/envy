package com.statemachinesystems.envy.common;

import com.statemachinesystems.envy.ValueParser;

public class MyCustomClassParser implements ValueParser<MyCustomClass> {

    @Override
    public MyCustomClass parseValue(String value) {
        String[] parts = value.split(":");
        return new MyCustomClass(parts[0], Integer.parseInt(parts[1]));
    }

    @Override
    public Class<MyCustomClass> getValueClass() {
        return MyCustomClass.class;
    }
}
