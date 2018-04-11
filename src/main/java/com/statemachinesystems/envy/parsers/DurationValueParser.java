package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link ValueParser} implementation for {@link java.time.Duration} values.
 *
 * <p>Supports the following grammar:</p>
 * <pre>
 *     ("-"|"+")? digit+ (whitespace? unit)?
 * </pre>
 *
 * Where <code>unit</code> must be one of the following lower-case labels:
 * <ul>
 *     <li><code>d</code>, <code>day</code> or <code>days</code></li>
 *     <li><code>h</code>, <code>hour</code> or <code>hours</code></li>
 *     <li><code>m</code>, <code>min</code>, <code>mins</code>, <code>minute</code> or <code>minutes</code></li>
 *     <li><code>s</code>, <code>sec</code>, <code>secs</code>, <code>second</code> or <code>seconds</code></li>
 *     <li><code>ms</code>, <code>milli</code>, <code>millis</code>, <code>millisecond</code> or <code>milliseconds</code></li>
 *     <li><code>us</code>, <code>&mu;s</code>, <code>micro</code>, <code>micros</code>, <code>microsecond</code> or <code>microsecond</code></li>
 *     <li><code>ns</code>, <code>nano</code>, <code>nanos</code>, <code>nanosecond</code> or <code>nanoseconds</code></li>
 * </ul>
 *
 * <p>Values without a unit label are treated as milliseconds.</p>
 */
public class DurationValueParser implements ValueParser<Duration> {

    private static Pattern pattern = Pattern.compile("([-+]?\\d+)(\\s*(\\p{Lower}+))?", Pattern.UNICODE_CHARACTER_CLASS);

    private static Map<String, TemporalUnit> units = new HashMap<>();

    static {
        for (String label : new String[] {"d", "day", "days"}) {
            units.put(label, ChronoUnit.DAYS);
        }
        for (String label : new String[] {"h", "hour", "hours"}) {
            units.put(label, ChronoUnit.HOURS);
        }
        for (String label : new String[] {"m", "min", "mins", "minute", "minutes"}) {
            units.put(label, ChronoUnit.MINUTES);
        }
        for (String label : new String[] {"s", "sec", "secs", "second", "seconds"}) {
            units.put(label, ChronoUnit.SECONDS);
        }
        for (String label : new String[] {"ms", "milli", "millis", "millisecond", "milliseconds"}) {
            units.put(label, ChronoUnit.MILLIS);
        }
        for (String label : new String[] {"us", "\u03BCs", "micro", "micros", "microsecond", "microseconds"}) {
            units.put(label, ChronoUnit.MICROS);
        }
        for (String label : new String[] {"ns", "nano", "nanos", "nanosecond", "nanoseconds"}) {
            units.put(label, ChronoUnit.NANOS);
        }
    }

    @Override
    public Duration parseValue(String value) {
        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + value);
        }
        long amount = Long.parseLong(matcher.group(1));
        String label = matcher.group(3);
        TemporalUnit unit = label == null ? ChronoUnit.MILLIS : units.get(label);
        if (unit == null) {
            throw new IllegalArgumentException("Invalid duration unit: " + label);
        }
        return Duration.of(amount, unit);
    }

    @Override
    public Class<Duration> getValueClass() {
        return Duration.class;
    }
}
