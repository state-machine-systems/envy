package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BigDecimalParserTest {

    @Test
    public void parsesBigDecimals() {
        BigDecimal value = BigDecimal.TEN;
        assertThat(new BigDecimalValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
