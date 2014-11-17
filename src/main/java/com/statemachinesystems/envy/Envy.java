package com.statemachinesystems.envy;

import com.statemachinesystems.envy.parsers.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.statemachinesystems.envy.Assertions.assertInterface;

public class Envy {

    public static <T> T configure(Class<T> configClass, ValueParser<?>... customValueParsers) {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>();
        valueParsers.addAll(defaultValueParsers());
        valueParsers.addAll(Arrays.asList(customValueParsers));
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);

        return new Envy(valueParserFactory, new DefaultConfigSource())
                .proxy(configClass);
    }

    public static List<ValueParser<?>> defaultValueParsers() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>();

        valueParsers.add(new BigDecimalValueParser());
        valueParsers.add(new BigIntegerValueParser());
        valueParsers.add(new BooleanValueParser());
        valueParsers.add(new ByteValueParser());
        valueParsers.add(new CharacterValueParser());
        valueParsers.add(new ClassValueParser());
        valueParsers.add(new DoubleValueParser());
        valueParsers.add(new FloatValueParser());
        valueParsers.add(new IntegerValueParser());
        valueParsers.add(new LongValueParser());
        valueParsers.add(new ShortValueParser());
        valueParsers.add(new StringValueParser());

        return Collections.unmodifiableList(valueParsers);
    }

    private final ValueParserFactory valueParserFactory;
    private final ConfigSource configSource;

    public Envy(ValueParserFactory valueParserFactory, ConfigSource configSource) {
        this.valueParserFactory = valueParserFactory;
        this.configSource = configSource;
    }

    public <T> T proxy(Class<T> configClass) {
        assertInterface(configClass);

        InvocationHandler invocationHandler = ProxyInvocationHandler.createInvocationHandler(
                        configClass, configSource, valueParserFactory);

        ClassLoader classLoader = configClass.getClassLoader();
        Class<?>[] proxyInterfaces = new Class<?>[] { configClass };

        @SuppressWarnings("unchecked")
        T proxyInstance = (T) Proxy.newProxyInstance(classLoader, proxyInterfaces, invocationHandler);

        return proxyInstance;
    }
}