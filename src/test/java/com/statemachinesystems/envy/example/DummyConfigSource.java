package com.statemachinesystems.envy.example;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

import java.util.Map;

public class DummyConfigSource implements ConfigSource {

    private final Map<Parameter, String> params;

    public DummyConfigSource(Map<Parameter, String> params) {
        this.params = params;
    }

    @Override
    public String getValue(Parameter parameter) {
        return params.get(parameter);
    }
}
