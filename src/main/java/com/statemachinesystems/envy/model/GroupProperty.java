package com.statemachinesystems.envy.model;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

import java.util.Map;
import java.util.Objects;

public class GroupProperty<T> implements Property<T> {

    private final Parameter parameter;
    private final Class<T> configClass;
    private final Map<String, Property<?>> propertiesByMethodName;

    public GroupProperty(Parameter parameter, Class<T> configClass, Map<String, Property<?>> propertiesByMethodName) {
        Objects.requireNonNull(configClass);
        Objects.requireNonNull(propertiesByMethodName);

        this.parameter = parameter;
        this.configClass = configClass;
        this.propertiesByMethodName = propertiesByMethodName;
    }

    public T extract(ConfigSource configSource) {
        throw new RuntimeException("lol");
    }
}
