package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import static com.statemachinesystems.envy.Assertions.assertEnum;

public class EnumValueParser<T extends Enum<T>> implements ValueParser<T> {

    private final Class<T> enumClass;

    public EnumValueParser(Class<T> enumClass) {
        assertEnum(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public T parseValue(String value) {
        for (Object constant : enumClass.getEnumConstants()) {
            if (value.equals(((Enum<?>) constant).name())) {
                @SuppressWarnings("unchecked")
                T enumValue = (T) constant;

                return enumValue;
            }
        }
        throw new IllegalArgumentException(
                String.format("No constant '%s' for enum %s", value, enumClass.getName()));
    }

    @Override
    public Class<T> getValueClass() {
        return enumClass;
    }
}
