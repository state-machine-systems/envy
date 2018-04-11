package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.math.BigInteger;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DurationValueParserTest {

    private final DurationValueParser parser = new DurationValueParser();

    @Test
    public void parsesDurationsWithoutLabelAsMilliseconds() {
        assertThat(parser.parseValue("5"), is(Duration.ofMillis(5)));
    }

    @Test
    public void parsesDurationInDays() {
        String[] variants = {"6d", "6 day", "6 days"};
        Duration expected = Duration.ofDays(6);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInHours() {
        String[] variants = {"12h", "12 hour", "12 hours"};
        Duration expected = Duration.ofHours(12);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInMinutes() {
        String[] variants = {"58m", "58 min", "58 mins", "58 minute", "58 minutes"};
        Duration expected = Duration.ofMinutes(58);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInSeconds() {
        String[] variants = {"1s", "1 sec", "1 secs", "1 second", "1 seconds"};
        Duration expected = Duration.ofSeconds(1);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInMilliseconds() {
        String[] variants = {"213ms", "213 milli", "213 millis", "213 millisecond", "213 milliseconds"};
        Duration expected = Duration.ofMillis(213);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInMicroseconds() {
        String[] variants = {"12345us", "12345\u03BCs", "12345 micro", "12345 micros", "12345 microsecond", "12345 microseconds"};
        Duration expected = Duration.ofNanos(12_345_000);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesDurationInNanoSeconds() {
        String[] variants = {"67890ns", "67890 nano", "67890 nanos", "67890 nanosecond", "67890 nanoseconds"};
        Duration expected = Duration.ofNanos(67890);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesNegativeDurations() {
        String[] variants = {"-10s", "-10 seconds"};
        Duration expected = Duration.ofSeconds(-10);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesExplicitlyPositiveDurations() {
        String[] variants = {"+100ms", "+100 millis"};
        Duration expected = Duration.ofMillis(100);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyString() {
        parser.parseValue("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingAmount() {
        parser.parseValue("seconds");
    }

    @Test(expected = NumberFormatException.class)
    public void rejectsOutOfRangeAmount() {
        BigInteger outOfRange = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        parser.parseValue(outOfRange + "ns");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownUnit() {
        parser.parseValue("18xx");
    }
}
