package com.statemachinesystems.envy.values;

import com.statemachinesystems.envy.ProxyInvocationHandler;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents fully extracted and parsed configuration values, ready for use in a {@link ProxyInvocationHandler}.
 */
public class ConfigMap implements Serializable {

    private final Map<String, ConfigValue> values;

    public ConfigMap(Map<String, ConfigValue> values) {
        this.values = values;
    }

    public ConfigValue getValue(String methodName) {
        return values.get(methodName);
    }

    public Set<String> getMethodNames() {
        return values.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigMap configMap = (ConfigMap) o;
        return Objects.equals(values, configMap.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
