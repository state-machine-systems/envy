package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ByteArrayTest extends FeatureTest {

    interface PrimitiveByteArray {
        byte[] bytes();
    }

    interface BoxedByteArray {
        Byte[] bytes();
    }

    @Test
    public void parsesPrimitiveArrayFromBase64() {
        byte[] expected = new byte[]{-7, -17, 20, 124, 107, 14, -83, 67, -70, 63, 87, -82, 1, 113, -29, 42};
        PrimitiveByteArray config = envy(configSource().add("bytes", "+e8UfGsOrUO6P1euAXHjKg=="))
                .proxy(PrimitiveByteArray.class);
        assertArrayEquals(expected, config.bytes());
    }

    @Test
    public void parsesBoxedArrayFromBase64() {
        Byte[] expected = new Byte[]{-76, 97, 104, 49, -75, 91, -2, -113, -107, 41, -79, 28, -80, 68, -22, 1};
        BoxedByteArray config = envy(configSource().add("bytes", "tGFoMbVb/o+VKbEcsETqAQ=="))
                .proxy(BoxedByteArray.class);
        assertArrayEquals(expected, config.bytes());
    }
}
