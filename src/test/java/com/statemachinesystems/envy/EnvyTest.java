package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.DummyConfigSource;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EnvyTest {

    public interface Config {
        String foo();
        String bar();
    }

    public interface AnotherConfig {
        String foo();
        String bar();
    }

    private ValueParserFactory valueParserFactory;
    private DummyConfigSource configSource;

    @Before
    public void setUp() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        valueParserFactory = new ValueParserFactory(valueParsers);

        configSource = new DummyConfigSource()
                .add("foo", "foo")
                .add("bar", "bar");
    }

    private Envy envy() {
        return new Envy(valueParserFactory, configSource);
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
        DummyConfigSource configSource1 = new DummyConfigSource()
                .add("foo", "foo1")
                .add("bar", "bar");
        DummyConfigSource configSource2 = new DummyConfigSource()
                .add("foo", "foo2")
                .add("bar", "bar");

        Config config1 = new Envy(valueParserFactory, configSource1).proxy(Config.class);
        Config config2 = new Envy(valueParserFactory, configSource2).proxy(Config.class);

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
        DummyConfigSource configSource1 = new DummyConfigSource()
                .add("foo", "foo1")
                .add("bar", "bar");
        DummyConfigSource configSource2 = new DummyConfigSource()
                .add("foo", "foo2")
                .add("bar", "bar");

        Config config1 = new Envy(valueParserFactory, configSource1).proxy(Config.class);
        Config config2 = new Envy(valueParserFactory, configSource2).proxy(Config.class);

        assertNotEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    public void proxyInstancesAreSerializable() throws IOException, ClassNotFoundException {
        Config config = envy().proxy(Config.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(config);

        Object deserialized = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();
        assertThat(deserialized, instanceOf(Config.class));
    }
}
