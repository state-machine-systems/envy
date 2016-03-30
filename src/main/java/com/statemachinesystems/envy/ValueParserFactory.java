package com.statemachinesystems.envy;

import com.statemachinesystems.envy.parsers.ArrayValueParser;
import com.statemachinesystems.envy.parsers.EnumValueParser;
import com.statemachinesystems.envy.parsers.ReflectionValueParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.statemachinesystems.envy.Conversions.toBoxed;

/**
 * Creates {@link com.statemachinesystems.envy.ValueParser} instances with special
 * support for arrays, enums and types with a constructor that takes
 * a single {@link java.lang.String} argument (see {@link com.statemachinesystems.envy.parsers.ReflectionValueParser}).
 */
public class ValueParserFactory {

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

    /**
     * Creates a {@link com.statemachinesystems.envy.ValueParser} for the given class.
     *
     * Returns null if no applicable parser is available.
     *
     * @param propertyClass  the class for which to create the {@link com.statemachinesystems.envy.ValueParser}
     * @return               a {@link com.statemachinesystems.envy.ValueParser} for the given class, or null if
     *                       no applicable parser is available
     */
    public ValueParser<?> getValueParser(Class<?> propertyClass) {
        return getValueParser(propertyClass, true);
    }

    private ValueParser<?> getValueParser(Class<?> propertyClass, boolean allowArrays) {
        ValueParser<?> parser = valueParsers.get(toBoxed(propertyClass));

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
            return ReflectionValueParser.parserOrNull(propertyClass);
        }
    }

    private ValueParser<?> enumValueParser(Class<?> propertyClass) {
        @SuppressWarnings("unchecked")
        ValueParser<?> enumValueParser = new EnumValueParser(propertyClass);
        return enumValueParser;
    }

    private ValueParser<?> arrayValueParser(Class<?> propertyClass) {
        Class<?> componentType = propertyClass.getComponentType();

        ValueParser<?> componentParser = getValueParser(componentType, false);
        if (componentParser == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        ValueParser<?> arrayValueParser = new ArrayValueParser(componentParser);
        return arrayValueParser;
    }
}
