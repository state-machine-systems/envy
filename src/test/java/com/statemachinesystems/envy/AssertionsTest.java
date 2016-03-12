package com.statemachinesystems.envy;

import org.junit.Test;

import java.lang.reflect.Method;

public class AssertionsTest {

    @SuppressWarnings("unused")
    private enum ExampleEnum {}

    @SuppressWarnings("unused")
    private interface ExampleInterface {
        String methodWithNoParameters();
        String methodWithParameters(String parameter1, String parameter2);
    }

    @Test
    public void detectsEnumClass() {
        Assertions.assertEnum(ExampleEnum.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonEnumClass() {
        Assertions.assertEnum(ExampleInterface.class);
    }

    @Test
    public void detectsInterfaceClass() {
        Assertions.assertInterface(ExampleInterface.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonInterfaceClass() {
        Assertions.assertInterface(ExampleEnum.class);
    }

    @Test
    public void detectsMethodWithNoParameters() throws NoSuchMethodException {
        Method method = ExampleInterface.class.getDeclaredMethod("methodWithNoParameters");
        Assertions.assertMethodWithNoParameters(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMethodWithParameters() throws NoSuchMethodException {
        Method method = ExampleInterface.class.getDeclaredMethod("methodWithParameters", String.class, String.class);
        Assertions.assertMethodWithNoParameters(method);
    }
}
