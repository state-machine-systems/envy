package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.util.HashMap;
import java.util.Map;

public class BooleanValueParser implements ValueParser<Boolean> {

    public static BooleanValueParser createParser(Map<String, Boolean> acceptedValues) {
        Map<String, Boolean> lowerCasedValues = new HashMap<String, Boolean>();
        for (Map.Entry<String, Boolean> entry : acceptedValues.entrySet()) {
            lowerCasedValues.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return new BooleanValueParser(lowerCasedValues);
    }

    private static Map<String, Boolean> defaultAcceptedValues() {
        Map<String, Boolean> acceptedValues = new HashMap<String, Boolean>();

        acceptedValues.put("true", Boolean.TRUE);
        acceptedValues.put("false", Boolean.FALSE);
        acceptedValues.put("yes", Boolean.TRUE);
        acceptedValues.put("no", Boolean.FALSE);
        acceptedValues.put("y", Boolean.TRUE);
        acceptedValues.put("n", Boolean.FALSE);
        acceptedValues.put("on", Boolean.TRUE);
        acceptedValues.put("off", Boolean.FALSE);

        return acceptedValues;
    }

    private final Map<String, Boolean> acceptedValues;

    public BooleanValueParser() {
        this(defaultAcceptedValues());
    }

    private BooleanValueParser(Map<String, Boolean> acceptedValues) {
        this.acceptedValues = acceptedValues;
    }

    @Override
    public Boolean parseValue(String value) {
        String key = value.toLowerCase();
        if (! acceptedValues.containsKey(key)) {
            throw new IllegalArgumentException(
                    String.format("No registered Boolean value for '%s'", value));
        }
        return acceptedValues.get(key);
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }
}
