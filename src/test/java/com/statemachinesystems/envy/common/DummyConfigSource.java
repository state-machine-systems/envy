package com.statemachinesystems.envy.common;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

import java.util.HashMap;
import java.util.Map;

public class DummyConfigSource implements ConfigSource {

    private final Map<Parameter, String> params;

    public DummyConfigSource() {
        this.params = new HashMap<Parameter, String>();
    }

    public DummyConfigSource add(String parameter, String value) {
        params.put(new Parameter(parameter), value);
        return this;
    }

    @Override
    public String getValue(Parameter parameter) {
        return params.get(parameter);
    }
}
