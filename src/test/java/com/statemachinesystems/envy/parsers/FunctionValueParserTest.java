package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.time.Instant;
import java.time.Period;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FunctionValueParserTest {

    class ParseFunction implements Function<String, Integer> {
        @Override
        public Integer apply(String value) {
            return parsePrefixedInt(value);
        }
    }

    private int parsePrefixedInt(String value) {
        return Integer.valueOf(value.substring(2));
    }

    @Test
    public void parsesPrimitiveUsingFunctionInstance() {
        FunctionValueParser<Integer> parser = new FunctionValueParser<>(new ParseFunction());
        assertThat(parser.parseValue("xx123"), is(123));
        assertThat(parser.getValueClass(), equalTo(Integer.class));
    }

    @Test
    public void parsesPrimitiveUsingMethodReference() {
        FunctionValueParser<Integer> parser = new FunctionValueParser<>(this::parsePrefixedInt);
        assertThat(parser.parseValue("xx123"), is(123));
        assertThat(parser.getValueClass(), equalTo(Integer.class));
    }

    @Test
    public void parsesPrimitiveUsingLambda() {
        FunctionValueParser<Integer> parser = new FunctionValueParser<>(i -> 5 + Integer.valueOf(i));
        assertThat(parser.parseValue("3"), is(8));
        assertThat(parser.getValueClass(), equalTo(Integer.class));
    }

    @Test
    public void parsesPeriodUsingLambda() {
        FunctionValueParser<Period> parser = new FunctionValueParser<>(Period::parse);
        assertThat(parser.parseValue("P2W"), is(Period.ofWeeks(2)));
        assertThat(parser.getValueClass(), equalTo(Period.class));
    }
}
