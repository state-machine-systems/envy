package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.FeatureTest;
import com.statemachinesystems.envy.common.DummyConfigSource;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EqualsAndHashCodeTest extends FeatureTest {

    @SuppressWarnings("unused")
    public interface Config {
        int foo();
        String bar();
    }

    @SuppressWarnings("unused")
    public interface AnotherConfig {
        int foo();
        String bar();
    }

    @Override
    public DummyConfigSource configSource() {
        return super.configSource().add("foo", "1").add("bar", "bar");
    }

    @Test
    public void equalsMethodOnProxyReturnsTrueForSameInstance() {
        Config config = envy().proxy(Config.class);
        assertEquals(config, config);
    }

    @Test
    public void equalsMethodOnProxyReturnsTrueForEqualInstances() {
        Config config1 = envy().proxy(Config.class);
        Config config2 = envy().proxy(Config.class);

        assertEquals(config1, config2);
        assertEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForUnequalInstances() {
        ConfigSource configSource1 = configSource();
        ConfigSource configSource2 = configSource().add("foo", "2");

        Config config1 = envy(configSource1).proxy(Config.class);
        Config config2 = envy(configSource2).proxy(Config.class);

        assertNotEquals(config1, config2);
        assertNotEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForInstanceOfAnotherProxyClass() {
        Config config1 = envy().proxy(Config.class);
        AnotherConfig config2 = envy().proxy(AnotherConfig.class);

        assertNotEquals(config1, config2);
        assertNotEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForUnrelatedProxyInstance() {
        ClassLoader classLoader = AnotherConfig.class.getClassLoader();
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return true;
            }
        };

        Config config1 = envy().proxy(Config.class);
        AnotherConfig config2 = (AnotherConfig) Proxy.newProxyInstance(classLoader,
                new Class<?>[]{AnotherConfig.class}, invocationHandler);

        assertNotEquals(config1, config2);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForInstanceOfAnotherNonProxyClass() {
        Config config = envy().proxy(Config.class);

        assertNotEquals(config, "some string");
    }

    @Test
    public void equalsMethodOnProxiesReturnsFalseForNullArgument() {
        Config config = envy().proxy(Config.class);
        assertFalse(config.equals(null));
    }

    @Test
    public void hashCodeMethodReturnsTheSameResultForEqualProxyInstances() {
        Config config1 = envy().proxy(Config.class);
        Config config2 = envy().proxy(Config.class);

        assertThat(config1.hashCode(), is(config2.hashCode()));
    }

    @Test
    public void hashCodeMethodReturnsDifferentResultsForUnequalProxyInstances() {
        ConfigSource configSource1 = configSource();
        ConfigSource configSource2 = configSource().add("foo", "2");

        Config config1 = envy(configSource1).proxy(Config.class);
        Config config2 = envy(configSource2).proxy(Config.class);

        assertNotEquals(config1.hashCode(), config2.hashCode());
    }
}
