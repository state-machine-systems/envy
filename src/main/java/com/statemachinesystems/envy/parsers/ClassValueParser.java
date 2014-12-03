package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Class} values.
 */
public class ClassValueParser implements ValueParser<Class> {

    private final ClassLoader classLoader;

    public ClassValueParser(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassValueParser() {
        this(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Class parseValue(String value) {
        try {
            final boolean initialize = true;
            return Class.forName(value, initialize, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Class<Class> getValueClass() {
        return Class.class;
    }
}
