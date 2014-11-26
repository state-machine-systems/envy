package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatternValueParserTest {

    @Test
    public void parsesPatterns() {
        String regex = "abc+";
        assertThat(new PatternValueParser().parseValue(regex).pattern(), is(Pattern.compile(regex).pattern()));
    }
}
