package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UrlValueParserTest {

    @Test
    public void parsesUrls() throws MalformedURLException {
        String url = "http://www.statemachinesystems.co.uk";
        assertThat(new UrlValueParser().parseValue(url), is(new URL(url)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rethrowsMalformedURLExceptionAsIllegalArgumentException() {
        new UrlValueParser().parseValue("");
    }
}
