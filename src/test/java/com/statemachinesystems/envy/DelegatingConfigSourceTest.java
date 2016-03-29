package com.statemachinesystems.envy;

import com.statemachinesystems.envy.common.StubConfigSource;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

public class DelegatingConfigSourceTest {

    @Test
    public void looksUpValuesInMultipleConfigSources() {
        ConfigSource source1 = new StubConfigSource().add("foo", "1");
        ConfigSource source2 = new StubConfigSource().add("bar", "2");
        ConfigSource configSource = new DelegatingConfigSource(source1, source2);

        assertThat(configSource.getValue(new Parameter("foo")), equalTo("1"));
        assertThat(configSource.getValue(new Parameter("bar")), equalTo("2"));
    }

    @Test
    public void choosesValuesInPreferenceOrder() {
        ConfigSource source1 = new StubConfigSource().add("foo", "1");
        ConfigSource source2 = new StubConfigSource().add("foo", "2");
        ConfigSource configSource = new DelegatingConfigSource(source1, source2);

        assertThat(configSource.getValue(new Parameter("foo")), equalTo("1"));
    }

    @Test
    public void missingValuesAreNull() {
        ConfigSource source1 = new StubConfigSource().add("foo", "1");
        ConfigSource source2 = new StubConfigSource().add("bar", "2");
        ConfigSource configSource = new DelegatingConfigSource(source1, source2);

        assertThat(configSource.getValue(new Parameter("baz")), nullValue());
    }
}
