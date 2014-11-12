package com.statemachinesystems.envy;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ConversionsTest {

    @Test
    public void convertsPrimitiveClassToBoxed() {
        assertEquals(Boolean.class, Conversions.toBoxed(boolean.class));
    }

    @Test
    public void leavesNonPrimitiveClassAloneWhenConvertingToBoxed() {
        assertEquals(String.class, Conversions.toBoxed(String.class));
    }

    @Test
    public void convertsBoxedClassToPrimitive() {
        assertEquals(int.class, Conversions.toPrimitive(Integer.class));
    }

    @Test
    public void leavesPrimitiveClassAloneWhenConvertingToPrimitive() {
        assertEquals(int.class, Conversions.toPrimitive(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonWrapperClassWhenConvertingToPrimitive() {
        Conversions.toPrimitive(String.class);
    }

    @Test
    public void convertsBoxedArrayToPrimitiveArray() {
        Integer[] input = new Integer[] { 1, 2, 3 };
        Object actual = Conversions.boxedArrayToPrimitiveArray(input);
        assertThat(actual, instanceOf(int[].class));
        int[] expected = new int[] { 1, 2, 3};
        assertArrayEquals(expected, (int[]) actual);
    }

    @Test
    public void leavesPrimitiveArrayAloneWhenConvertingToPrimitiveArray() {
        int[] expected = new int[] { 1, 2, 3 };
        assertArrayEquals(expected, (int[]) Conversions.boxedArrayToPrimitiveArray(expected));
    }
}
