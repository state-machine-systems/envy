package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CharacterValueParserTest {

    @Test
    public void parsesSingleCharacterStrings() {
        char value = 'X';
        assertThat(new CharacterValueParser().parseValue(String.valueOf(value)), is(value));
    }

    @Test
    public void ignoresTrailingCharacters() {
        String input = "hello";
        char expected = 'h';
        assertThat(new CharacterValueParser().parseValue(input), is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyString() {
        new CharacterValueParser().parseValue("");
    }
}
