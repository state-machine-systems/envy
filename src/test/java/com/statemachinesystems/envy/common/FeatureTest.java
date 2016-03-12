package com.statemachinesystems.envy.common;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Envy;
import com.statemachinesystems.envy.ValueParser;
import com.statemachinesystems.envy.ValueParserFactory;
import com.statemachinesystems.envy.common.DummyConfigSource;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureTest {

    protected Envy envy() {
        return envy(valueParserFactory(), configSource());
    }

    protected Envy envy(ValueParserFactory valueParserFactory) {
        return envy(valueParserFactory, configSource());
    }

    protected Envy envy(ConfigSource configSource) {
        return envy(valueParserFactory(), configSource);
    }

    protected Envy envy(ValueParserFactory valueParserFactory, ConfigSource configSource) {
        return new Envy(valueParserFactory, configSource);
    }

    protected ValueParserFactory valueParserFactory() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        return new ValueParserFactory(valueParsers);
    }

    protected DummyConfigSource configSource() {
        return new DummyConfigSource();
    }
}
