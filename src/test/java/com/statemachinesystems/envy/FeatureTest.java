package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.DummyConfigSource;

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
