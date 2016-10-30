package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.*;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class NestingTest extends FeatureTest {

    public interface Outer1 {
        Inner1 foo();
    }

    public interface Inner1 {
        int bar();
    }

    public interface Outer2 {
        interface Inner2 {
            Inner1 baz();
        }

        Inner2 foo();
    }

    public interface Outer3 {
        interface Inner3 {
            int bar();
            int baz();
        }
        @Nullable
        Inner3 foo();
    }

    public interface Outer4 {
        interface Inner4 {
            int bar();

            @Nullable
            String baz();
        }
        @Nullable
        Inner4 foo();
    }

    public interface Outer5 {
        interface Inner5 {
            int baz();
        }
        @Name("bar")
        Inner5 foo();
    }

    @Prefix("foo")
    public interface Outer6 {
        @Prefix("xxx")
        interface Inner6 {
            int baz();
        }
        Inner6 bar();
    }

    @Prefix("foo")
    public interface Outer7 {
        interface Inner7 {
            @Name("baz")
            int qux();
        }
        @Name("bar")
        Inner7 baz();
    }

    public interface Outer8 {
        @Default("xxx")
        Inner1 foo();
    }

    public interface Recursive {
        String foo();

        @SuppressWarnings("unused")
        Recursive inner();
    }

    @Test
    public void populatesConfigWithOneLevelOfNesting() {
        ConfigSource configSource = configSource().add("foo.bar", "1");
        Outer1 config = envy(configSource).proxy(Outer1.class);
        assertThat(config.foo().bar(), equalTo(1));
    }

    @Test
    public void populatesConfigWithTwoLevelsOfNesting() {
        ConfigSource configSource = configSource().add("foo.baz.bar", "1");
        Outer2 config = envy(configSource).proxy(Outer2.class);
        assertThat(config.foo().baz().bar(), equalTo(1));
    }

    @Test(expected = MissingParameterValueException.class)
    public void mandatoryNestedConfigWithNoValuesProvidedThrowsException() {
        envy().proxy(Outer1.class);
    }

    @Test
    public void nullableNestedConfigWithAllValuesProvidedIsPopulated() {
        ConfigSource configSource = configSource().add("foo.bar", "1").add("foo.baz", "2");
        Outer3 config = envy(configSource).proxy(Outer3.class);
        assertThat(config.foo().bar(), equalTo(1));
        assertThat(config.foo().baz(), equalTo(2));
    }

    @Test
    public void nullableNestedConfigWithNoValuesProvidedIsNull() {
        Outer3 config = envy().proxy(Outer3.class);
        assertThat(config.foo(), nullValue());
    }

    @Test
    public void nullableNestedConfigWithSomeValuesProvidedIsNull() {
        ConfigSource configSource = configSource().add("foo.bar", "1");
        Outer3 config = envy(configSource).proxy(Outer3.class);
        assertThat(config.foo(), nullValue());
    }

    @Test
    public void nullableNestedConfigWithAllMandatoryValuesProvidedIsPopulated() {
        ConfigSource configSource = configSource().add("foo.bar", "1");
        Outer4 config = envy(configSource).proxy(Outer4.class);
        assertThat(config.foo().bar(), equalTo(1));
        assertThat(config.foo().baz(), nullValue());
    }

    @Test
    public void appliesCustomNameToNestedConfig() {
        ConfigSource configSource = configSource().add("bar.baz", "1");
        Outer5 config = envy(configSource).proxy(Outer5.class);
        assertThat(config.foo().baz(), equalTo(1));
    }

    @Test
    public void appliesPrefixOnOuterInterfaceOnly() {
        ConfigSource configSource = configSource().add("foo.bar.baz", "1");
        Outer6 config = envy(configSource).proxy(Outer6.class);
        assertThat(config.bar().baz(), equalTo(1));
    }

    @Test
    public void appliesPrefixAndCustomNamesToNestedConfig() {
        ConfigSource configSource = configSource().add("foo.bar.baz", "1");
        Outer7 config = envy(configSource).proxy(Outer7.class);
        assertThat(config.baz().qux(), equalTo(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsDefaultAnnotationOnNestedConfig() {
        ConfigSource configSource = configSource().add("foo.bar", "1");
        envy(configSource).proxy(Outer8.class);
    }

    @Test(expected = MissingParameterValueException.class)
    public void rejectsRecursiveNesting() {
        ConfigSource configSource = configSource().add("foo", "1").add("inner.foo", "2");
        envy(configSource).proxy(Recursive.class);
    }
}
