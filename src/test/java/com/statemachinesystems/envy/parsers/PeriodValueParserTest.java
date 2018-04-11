package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.time.Period;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PeriodValueParserTest {

    private final PeriodValueParser parser = new PeriodValueParser();

    @Test
    public void parsesPeriodsWithoutLabelAsDays() {
        assertThat(parser.parseValue("5"), is(Period.ofDays(5)));
    }

    @Test
    public void parsesPeriodInDays() {
        String[] variants = {"6d", "6 day", "6 days"};
        Period expected = Period.ofDays(6);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesPeriodInWeeks() {
        String[] variants = {"9w", "9 week", "9 weeks"};
        Period expected = Period.ofWeeks(9);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesPeriodInMonths() {
        String[] variants = {"3m", "3mo", "3 month", "3 months"};
        Period expected = Period.ofMonths(3);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesPeriodInYears() {
        String[] variants = {"12y", "12 year", "12 years"};
        Period expected = Period.ofYears(12);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesNegativePeriods() {
        String[] variants = {"-10d", "-10 days"};
        Period expected = Period.ofDays(-10);
        for (String value : variants) {
            assertThat(parser.parseValue(value), is(expected));
        }
    }

    @Test
    public void parsesExplicitlyPositivePeriods() {
        String[] variants = {"+1w", "+1 week"};
        Period expected = Period.ofWeeks(1);
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
        parser.parseValue("years");
    }

    @Test(expected = NumberFormatException.class)
    public void rejectsOutOfRangeAmount() {
        long outOfRange = Integer.MAX_VALUE + 1L;
        parser.parseValue(outOfRange + "d");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownUnit() {
        parser.parseValue("18xx");
    }
}
