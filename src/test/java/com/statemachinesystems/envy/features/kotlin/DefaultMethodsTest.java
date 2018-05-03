package com.statemachinesystems.envy.features.kotlin;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultMethodsTest extends FeatureTest {

    @Test
    public void grandparentUsesMethodImplementation() {
        Grandparent config = envy().proxy(Grandparent.class);
        assertThat(config.x(), is("Grandparent x"));
    }

    @Test
    public void grandparentUsesProvidedConfig() {
        Grandparent config = envy(configSource().add("x", "provided")).proxy(Grandparent.class);
        assertThat(config.x(), is("provided"));
    }

    @Test
    public void parentUsesOverriddenMethodImplementation() {
        Parent1 config = envy().proxy(Parent1.class);
        assertThat(config.x(), is("Parent1 x"));
    }

    @Test
    public void parentUsesProvidedConfig() {
        Parent1 config = envy(configSource().add("x", "provided")).proxy(Parent1.class);
        assertThat(config.x(), is("provided"));
    }

    @Test
    public void parentUsesMethodImplementation() {
        Parent2 config = envy().proxy(Parent2.class);
        assertThat(config.x(), is("Grandparent x"));
        assertThat(config.y(), is("Parent2 y"));
    }

    @Test
    public void parentUsesPartialConfig() {
        Parent2 config = envy(configSource().add("y", "provided")).proxy(Parent2.class);
        assertThat(config.x(), is("Grandparent x"));
        assertThat(config.y(), is("provided"));
    }

    @Test
    public void childInheritsAllParentMethodImplementations() {
        Child1 config = envy().proxy(Child1.class);
        assertThat(config.x(), is("Parent1 x"));
        assertThat(config.y(), is("Parent2 y"));
    }

    @Test
    public void childUsesProvidedConfig() {
        ConfigSource configSource = configSource().add("x", "provided x").add("y", "provided y");
        Child1 config = envy(configSource).proxy(Child1.class);
        assertThat(config.x(), is("provided x"));
        assertThat(config.y(), is("provided y"));
    }

    @Test
    public void childUsesOverriddenMethodImplementation() {
        Child2 config = envy().proxy(Child2.class);
        assertThat(config.x(), is("Child2 x"));
        assertThat(config.y(), is("Parent2 y"));
    }

    @Test
    public void childUsesPartialConfig() {
        Child2 config = envy(configSource().add("y", "provided")).proxy(Child2.class);
        assertThat(config.x(), is("Child2 x"));
        assertThat(config.y(), is("provided"));
    }

    @Test
    public void optionalWrapperUsesDefaultMethod() {
        WithOptionalWrapper config = envy().proxy(WithOptionalWrapper.class);
        assertThat(config.getValue(), is(Optional.of("default value")));
    }

    @Test
    public void optionalWrapperUsesConfigValue() {
        WithOptionalWrapper config = envy(configSource().add("value", "from config")).proxy(WithOptionalWrapper.class);
        assertThat(config.getValue(), is(Optional.of("from config")));
    }

    @Test
    public void nestedViaDefaultMethodUsesDefaultMethod() {
        ConfigSource configSource = configSource().add("nested.foo", "provided foo");
        NestedViaDefaultMethod config = envy(configSource).proxy(NestedViaDefaultMethod.class);
        assertThat(config.getNested().getFoo(), is("default foo"));
        assertThat(config.getNested().getBar(), is("default bar"));
    }

    @Test
    public void nestedViaDefaultMethodUsesProvidedConfig() {
        ConfigSource configSource = configSource()
                .add("nested.foo", "provided foo")
                .add("nested.bar", "provided bar");
        NestedViaDefaultMethod config = envy(configSource).proxy(NestedViaDefaultMethod.class);
        assertThat(config.getNested().getFoo(), is("provided foo"));
        assertThat(config.getNested().getBar(), is("provided bar"));
    }

    @Test
    public void nestedUsingDefaultMethodsOnValueUsesDefaultMethods() {
        NestedUsingDefaultMethodsOnValue config = envy().proxy(NestedUsingDefaultMethodsOnValue.class);
        assertThat(config.getNested().getFoo(), is("nested default foo"));
        assertThat(config.getNested().getBar(), is("nested default bar"));
    }

    @Test
    public void nestedUsingDefaultMethodsOnValueUsesDefaultMethodsAndProvidedConfig() {
        ConfigSource configSource = configSource().add("nested.foo", "provided foo");
        NestedUsingDefaultMethodsOnValue config = envy(configSource).proxy(NestedUsingDefaultMethodsOnValue.class);
        assertThat(config.getNested().getFoo(), is("provided foo"));
        assertThat(config.getNested().getBar(), is("nested default bar"));
    }

    @Test
    public void nestedUsingDefaultMethodsOnValueUsesProvidedConfig() {
        ConfigSource configSource = configSource()
                .add("nested.foo", "provided foo")
                .add("nested.bar", "provided bar");
        NestedUsingDefaultMethodsOnValue config = envy(configSource).proxy(NestedUsingDefaultMethodsOnValue.class);
        assertThat(config.getNested().getFoo(), is("provided foo"));
        assertThat(config.getNested().getBar(), is("provided bar"));
    }

    @Test
    public void nestedOverridingDefaultMethodOnValueUsesDefaultMethods() {
        NestedOverridingDefaultMethodOnValue config = envy().proxy(NestedOverridingDefaultMethodOnValue.class);
        assertThat(config.getNested().getFoo(), is("nested default foo"));
        assertThat(config.getNested().getBar(), is("overridden bar"));
    }

    @Test
    public void nestedOverridingDefaultMethodOnValueUsesDefaultMethodsAndProvidedConfig() {
        ConfigSource configSource = configSource().add("nested.foo", "provided foo");
        NestedOverridingDefaultMethodOnValue config = envy(configSource).proxy(NestedOverridingDefaultMethodOnValue.class);
        assertThat(config.getNested().getFoo(), is("provided foo"));
        assertThat(config.getNested().getBar(), is("nested default bar"));
    }

    @Test
    public void nestedOverridingDefaultMethodOnValueUsesProvidedConfig() {
        ConfigSource configSource = configSource()
                .add("nested.foo", "provided foo")
                .add("nested.bar", "provided bar");
        NestedOverridingDefaultMethodOnValue config = envy(configSource).proxy(NestedOverridingDefaultMethodOnValue.class);
        assertThat(config.getNested().getFoo(), is("provided foo"));
        assertThat(config.getNested().getBar(), is("provided bar"));
    }
}
