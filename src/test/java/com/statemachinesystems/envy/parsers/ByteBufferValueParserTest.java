package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ByteBufferValueParserTest {

    private ByteBufferValueParser valueParser = new ByteBufferValueParser();

    private ByteBuffer example = ByteBuffer.wrap(new byte[]{
            -73, 124, -114, 28, 48, -104, 84, 127, -66, 67, -3, 9, -19, -107, -124, 102
    });

    @Test
    public void parsesBase64() {
        assertThat(valueParser.parseValue("t3yOHDCYVH++Q/0J7ZWEZg=="), is(example));
    }

    @Test
    public void parsesUrlSafeBase64() {
        assertThat(valueParser.parseValue("t3yOHDCYVH--Q_0J7ZWEZg=="), is(example));
    }
}
