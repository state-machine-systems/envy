package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

public class ClassValueParserTest {

    @Test
    public void parsersClasses() {
        assertEquals(SecureRandom.class, new ClassValueParser().parseValue("java.security.SecureRandom"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rethrowsClassNotFoundExceptionAsIllegalArgumentException() {
        new ClassValueParser().parseValue("com.foo.Missing");
    }
}
