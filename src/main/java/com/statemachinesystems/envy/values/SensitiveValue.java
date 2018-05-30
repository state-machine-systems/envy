package com.statemachinesystems.envy.values;

public class SensitiveValue<T> extends ResolvedValue<T> {

    public static final String MASKED_VALUE = "******";

    protected SensitiveValue(T value, Status status) {
        super(value, status);
    }

    @Override
    public String format(Object proxy) {
        return getStatus() == Status.MISSING
                ? super.format(proxy)
                : MASKED_VALUE;
    }
}
