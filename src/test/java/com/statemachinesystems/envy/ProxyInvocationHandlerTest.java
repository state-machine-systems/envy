package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.DummyConfigSource;
import com.statemachinesystems.envy.parsers.IntegerValueParser;
import com.statemachinesystems.envy.parsers.StringValueParser;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProxyInvocationHandlerTest {

    public static interface ExampleConfig {
        String getAString();
        Integer getABoxedInteger();
        int getAPrimitiveInteger();
        Integer[] getAnArrayOfBoxedIntegers();
        int[] getAnArrayOfPrimitiveIntegers();
    }

    private ConfigSource configSource;
    private ValueParserFactory valueParserFactory;
    private ProxyInvocationHandler invocationHandler;

    @Before
    public void setUp() {
        Map<Parameter, String> params = new HashMap<Parameter, String>();
        params.put(new Parameter("A_STRING"), "foo");
        params.put(new Parameter("A_BOXED_INTEGER"), "10");
        params.put(new Parameter("A_PRIMITIVE_INTEGER"), "15");
        params.put(new Parameter("AN_ARRAY_OF_BOXED_INTEGERS"), "7");
        params.put(new Parameter("AN_ARRAY_OF_PRIMITIVE_INTEGERS"), "1,2,3");
        configSource = new DummyConfigSource(params);

        valueParserFactory = new ValueParserFactory(new StringValueParser(), new IntegerValueParser());

        invocationHandler = ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, configSource, valueParserFactory);
    }

    @Test
    public void retrievesStringProperty() throws Throwable {
        assertEquals("foo", invoke("getAString"));
    }

    @Test
    public void retrievesBoxedIntegerProperty() throws Throwable {
        assertEquals(10, invoke("getABoxedInteger"));
    }

    @Test
    public void retrievesPrimitiveIntegerProperty() throws Throwable {
        assertEquals(15, invoke("getAPrimitiveInteger"));
    }

    @Test
    public void retrievesArrayOfBoxedInteger() throws Throwable {
        assertArrayEquals(new Integer[]{ 7 }, (Integer[]) invoke("getAnArrayOfBoxedIntegers"));
    }

    @Test
    public void retrievesArrayOfPrimitiveIntegers() throws Throwable {
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) invoke("getAnArrayOfPrimitiveIntegers"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingParameter() {
        ConfigSource emptyConfigSource = new DummyConfigSource(Collections.<Parameter, String>emptyMap());
        ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, emptyConfigSource, valueParserFactory);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsUnsupportedType() {
        ValueParserFactory emptyValueParserFactory = new ValueParserFactory();
        ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, configSource, emptyValueParserFactory);
    }

    private Object invoke(String methodName) throws Throwable {
        Method method = ExampleConfig.class.getMethod(methodName);
        return invocationHandler.invoke(null, method, new Object[] {});
    }
}
