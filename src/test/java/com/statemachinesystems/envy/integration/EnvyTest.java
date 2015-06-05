package com.statemachinesystems.envy.integration;

import com.statemachinesystems.envy.*;
import com.statemachinesystems.envy.example.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EnvyTest {

    public interface ExampleConfig {
        String getNodeType();
        int getSplineReticulationCount();
        String[] getEndpoints();
        MyEnum[] getFooBars();
        boolean isDirigibleEnabled();
        MyClass[] dataPoints();
        MyCustomClass[] moreDataPoints();

        @Default("ignored")
        MyClass ignoresTheDefault();

        @Default("j:47")
        MyCustomClass usesTheDefault();

        @Name("com.foo.long.and.awkward.name")
        String withCustomName();

        @Optional
        String mightBeNull();
    }

    public interface MinimalConfig1 {
        String foo();
        String bar();
    }

    public interface MinimalConfig2 {
        String foo();
        String bar();
    }

    private ExampleConfig config;

    @Before
    public void setUp() {
        config = Envy.configure(ExampleConfig.class, new MyCustomClassParser());
    }

    @Test
    public void retrievesNodeTypeFromEnvironment() {
        assertEquals("mellifluator", config.getNodeType());
    }

    @Test
    public void retreivesSplineReticulationCountFromEnvironment() {
        assertEquals(143, config.getSplineReticulationCount());
    }

    @Test
    public void retrievesEndpointsFromSystemProperty() {
        assertArrayEquals(new String[] { "foo17", "foo18", "foo19"}, config.getEndpoints());
    }

    @Test
    public void retrievesFooBarsFromSystemProperty() {
        assertArrayEquals(new MyEnum[] { MyEnum.FOO, MyEnum.BAR }, config.getFooBars());
    }

    @Test
    public void retrievesDirigibleEnabledFromOverridingSystemProperty() {
        assertTrue(config.isDirigibleEnabled());
    }

    @Test
    public void retrievesDataPointsFromOverridingSystemProperty() {
        assertArrayEquals(new MyClass[]{new MyClass("0"), new MyClass("1"), new MyClass("2")},
                config.dataPoints());
    }

    @Test
    public void retrievesMoreDataPointsFromOverridingSystemProperty() {
        assertArrayEquals(new MyCustomClass[] { new MyCustomClass("x", 7), new MyCustomClass("y", 18), new MyCustomClass("z", 41) },
                config.moreDataPoints());
    }

    @Test
    public void ignoresDefaultValueWhenSystemPropertyIsAvailable() {
        assertEquals(new MyClass("foo-bar-63"), config.ignoresTheDefault());
    }

    @Test
    public void usesDefaultValueWhenNoSystemPropertyOrEnvironmentVariableIsAvailable() {
        assertEquals(new MyCustomClass("j", 47), config.usesTheDefault());
    }

    @Test
    public void usesCustomNameAnnotation() {
        assertEquals("xyz", config.withCustomName());
    }

    @Test
    public void returnsNullForUndefinedOptionalParameter() {
        assertNull(config.mightBeNull());
    }

    @Test
    public void equalsMethodOnProxyReturnsTrueForSameInstance() {
        assertEquals(config, config);
    }

    @Test
    public void equalsMethodOnProxyReturnsTrueForEqualInstances() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);
        DummyConfigSource configSource = new DummyConfigSource()
                .add("foo", "foo")
                .add("bar", "bar");
        Envy envy = new Envy(valueParserFactory, configSource);

        MinimalConfig1 config1 = envy.proxy(MinimalConfig1.class);
        MinimalConfig1 config2 = envy.proxy(MinimalConfig1.class);

        assertEquals(config1, config2);
        assertEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForUnequalInstances() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);
        DummyConfigSource configSource1 = new DummyConfigSource()
                .add("foo", "foo1")
                .add("bar", "bar");
        DummyConfigSource configSource2 = new DummyConfigSource()
                .add("foo", "foo2")
                .add("bar", "bar");

        MinimalConfig1 config1 = new Envy(valueParserFactory, configSource1).proxy(MinimalConfig1.class);
        MinimalConfig1 config2 = new Envy(valueParserFactory, configSource2).proxy(MinimalConfig1.class);

        assertNotEquals(config1, config2);
        assertNotEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForInstanceOfAnotherProxyClass() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);
        DummyConfigSource configSource = new DummyConfigSource()
                .add("foo", "foo")
                .add("bar", "bar");
        Envy envy = new Envy(valueParserFactory, configSource);

        MinimalConfig1 config1 = envy.proxy(MinimalConfig1.class);
        MinimalConfig2 config2 = envy.proxy(MinimalConfig2.class);

        assertNotEquals(config1, config2);
        assertNotEquals(config2, config1);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForUnrelatedProxyInstance() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);
        DummyConfigSource configSource = new DummyConfigSource()
                .add("foo", "foo")
                .add("bar", "bar");
        ClassLoader classLoader = MinimalConfig2.class.getClassLoader();
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return true;
            }
        };

        MinimalConfig1 config1 = new Envy(valueParserFactory, configSource).proxy(MinimalConfig1.class);
        MinimalConfig2 config2 = (MinimalConfig2) Proxy.newProxyInstance(classLoader,
                new Class<?>[]{ MinimalConfig2.class }, invocationHandler);

        assertNotEquals(config1, config2);
    }

    @Test
    public void equalsMethodOnProxyReturnsFalseForInstanceOfAnotherNonProxyClass() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>(Envy.defaultValueParsers());
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);
        DummyConfigSource configSource = new DummyConfigSource()
                .add("foo", "foo")
                .add("bar", "bar");

        MinimalConfig1 config = new Envy(valueParserFactory, configSource).proxy(MinimalConfig1.class);

        assertNotEquals(config, "some string");
    }

    @Test
    public void equalsMethodOnProxiesReturnsFalseForNullArgument() {
        assertFalse(config.equals(null));
    }
}
