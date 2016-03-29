package com.statemachinesystems.envy;

import com.statemachinesystems.envy.common.StubConfigSource;
import com.statemachinesystems.envy.sources.DelegatingConfigSource;
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

    @Test
    public void looksUpValuesInNestedConfigSources() {
        ConfigSource child1 = new StubConfigSource().add("foo", "1");
        ConfigSource child2 = new StubConfigSource().add("bar", "2");
        ConfigSource parent1 = new DelegatingConfigSource(child1, child2);

        ConfigSource child3 = new StubConfigSource().add("foo", "3");
        ConfigSource child4 = new StubConfigSource().add("bar", "4");
        ConfigSource child5 = new StubConfigSource().add("baz", "5");
        ConfigSource parent2 = new DelegatingConfigSource(child3, child4, child5);

        ConfigSource configSource = new DelegatingConfigSource(parent1, parent2);

        assertThat(configSource.getValue(new Parameter("foo")), equalTo("1"));
        assertThat(configSource.getValue(new Parameter("bar")), equalTo("2"));
        assertThat(configSource.getValue(new Parameter("baz")), equalTo("5"));
    }
}
