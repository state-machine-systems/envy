package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link ValueParser} implementation for {@link java.time.Period} values.
 * <p>
 * <p>Supports the following grammar:</p>
 * <pre>
 *     ("-"|"+")? digit+ (whitespace? unit)?
 * </pre>
 * <p>
 * Where <code>unit</code> must be one of the following lower-case labels:
 * <ul>
 * <li><code>d</code>, <code>day</code> or <code>days</code></li>
 * <li><code>w</code>, <code>week</code> or <code>weeks</code></li>
 * <li><code>m</code>, <code>mo</code>, <code>month</code> or <code>months</code>
 * <li><code>y</code>, <code>year</code> or <code>years</code></li>
 * </ul>
 * <p>
 * <p>Values without a unit label are treated as days.</p>
 */
public class PeriodValueParser implements ValueParser<Period> {

    private static Pattern pattern = Pattern.compile("([-+]?\\d+)(\\s*(\\p{Lower}+))?");

    private static Map<String, ChronoUnit> units = new HashMap<>();

    static {
        for (String label : new String[]{"d", "day", "days"}) {
            units.put(label, ChronoUnit.DAYS);
        }
        for (String label : new String[]{"w", "week", "weeks"}) {
            units.put(label, ChronoUnit.WEEKS);
        }
        for (String label : new String[]{"m", "mo", "month", "months"}) {
            units.put(label, ChronoUnit.MONTHS);
        }
        for (String label : new String[]{"y", "year", "years"}) {
            units.put(label, ChronoUnit.YEARS);
        }
    }

    @Override
    public Period parseValue(String value) {
        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid period format: " + value);
        }

        int amount = Integer.parseInt(matcher.group(1));
        String label = matcher.group(3);
        ChronoUnit unit = label == null ? ChronoUnit.DAYS : units.get(label);
        if (unit == null) {
            throw new IllegalArgumentException("Invalid period unit: " + label);
        }

        switch (unit) {
            case DAYS:
                return Period.ofDays(amount);
            case WEEKS:
                return Period.ofWeeks(amount);
            case MONTHS:
                return Period.ofMonths(amount);
            case YEARS:
                return Period.ofYears(amount);
            default:
                throw new IllegalStateException("Unsupported unit:" + unit);
        }
    }

    @Override
    public Class<Period> getValueClass() {
        return Period.class;
    }
}
