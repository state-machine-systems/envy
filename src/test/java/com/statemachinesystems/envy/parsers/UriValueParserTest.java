package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UriValueParserTest {

    @Test
    public void parsesUris() {
        String uri = "tel:555-5555";
        assertThat(new UriValueParser().parseValue(uri), is(URI.create(uri)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rethrowsURISyntaxExceptionAsIllegalArgumentException() {
        new UriValueParser().parseValue(":");
    }
}
