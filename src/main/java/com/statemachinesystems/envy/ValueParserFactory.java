package com.statemachinesystems.envy;

import com.statemachinesystems.envy.parsers.ArrayValueParser;
import com.statemachinesystems.envy.parsers.EnumValueParser;
import com.statemachinesystems.envy.parsers.ReflectionValueParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ValueParserFactory {

    private static final Map<Class<?>, Class<?>> primitiveToBoxed = new HashMap<Class<?>, Class<?>>();

    {
        primitiveToBoxed.put(boolean.class, Boolean.class);
        primitiveToBoxed.put(byte.class, Byte.class);
        primitiveToBoxed.put(char.class, Character.class);
        primitiveToBoxed.put(double.class, Double.class);
        primitiveToBoxed.put(float.class, Float.class);
        primitiveToBoxed.put(int.class, Integer.class);
        primitiveToBoxed.put(long.class, Long.class);
        primitiveToBoxed.put(short.class, Short.class);
        primitiveToBoxed.put(void.class, Void.class);
    }

    private static Class<?> boxed(Class<?> propertyClass) {
        return propertyClass.isPrimitive()
                ? primitiveToBoxed.get(propertyClass)
                : propertyClass;
    }

    private final Map<Class<?>, ValueParser<?>> valueParsers;

    public ValueParserFactory(ValueParser<?>... valueParsers) {
        this(Arrays.asList(valueParsers));
    }

    public ValueParserFactory(Collection<ValueParser<?>> valueParsers) {
        this.valueParsers = new HashMap<Class<?>, ValueParser<?>>();

        for (ValueParser<?> parser : valueParsers) {
            this.valueParsers.put(parser.getValueClass(), parser);
        }
    }

    public ValueParser<?> getValueParser(Class<?> propertyClass) {
        return getValueParser(propertyClass, true);
    }

    private ValueParser<?> getValueParser(Class<?> propertyClass, boolean allowArrays) {
        ValueParser<?> parser = valueParsers.get(boxed(propertyClass));

        if (parser != null) {
            return parser;
        } else if (propertyClass.isEnum()) {
            return enumValueParser(propertyClass);
        } else if (propertyClass.isArray()) {
            if (! allowArrays) {
                throw new UnsupportedTypeException("Nested arrays are not supported");
            }
            return arrayValueParser(propertyClass);
        } else {
            parser = ReflectionValueParser.parserOrNull(propertyClass);
            if (parser == null) {
                throw new UnsupportedTypeException(
                        String.format("Cannot parse value of class %s", propertyClass.getCanonicalName()));
            }
            return parser;
        }
    }

    private ValueParser<?> enumValueParser(Class<?> propertyClass) {
        @SuppressWarnings("unchecked")
        ValueParser<?> enumValueParser = new EnumValueParser(propertyClass);

        return enumValueParser;
    }

    private ValueParser<?> arrayValueParser(Class<?> propertyClass) {
        Class<?> componentType = propertyClass.getComponentType();

        @SuppressWarnings("unchecked")
        ValueParser<?> arrayValueParser =
                new ArrayValueParser(getValueParser(componentType, false));

        return arrayValueParser;
    }
}
