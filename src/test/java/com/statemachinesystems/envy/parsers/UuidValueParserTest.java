package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UuidValueParserTest {

    @Test
    public void parsesUuids() {
        UUID uuid = UUID.randomUUID();
        assertThat(new UuidValueParser().parseValue(uuid.toString()), is(uuid));
    }
}
