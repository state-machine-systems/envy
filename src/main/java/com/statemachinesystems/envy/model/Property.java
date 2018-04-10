package com.statemachinesystems.envy.model;

import com.statemachinesystems.envy.ConfigSource;

public interface Property<T> {

    T extract(ConfigSource configSource);
}
