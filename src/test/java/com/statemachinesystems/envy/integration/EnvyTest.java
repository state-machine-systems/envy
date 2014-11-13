package com.statemachinesystems.envy.integration;

import com.statemachinesystems.envy.Default;
import com.statemachinesystems.envy.Envy;
import com.statemachinesystems.envy.example.MyClass;
import com.statemachinesystems.envy.example.MyCustomClass;
import com.statemachinesystems.envy.example.MyCustomClassParser;
import com.statemachinesystems.envy.example.MyEnum;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnvyTest {

    public static interface ExampleConfig {
        String getNodeType();
        int getSplineReticulationCount();
        String[] getEndpoints();
        MyEnum[] getFooBars();
        boolean isDirigibleEnabled();
        MyClass[] dataPoints();
        MyCustomClass[] moreDataPoints();

        @Default("ignored")
        MyClass ignoresTheDefault();

        @Default("j:47")
        MyCustomClass usesTheDefault();
    }

    private ExampleConfig config;

    @Before
    public void setUp() {
        config = Envy.configure(ExampleConfig.class, new MyCustomClassParser());
    }

    @Test
    public void retrievesNodeTypeFromEnvironment() {
        assertEquals("mellifluator", config.getNodeType());
    }

    @Test
    public void retreivesSplineReticulationCountFromEnvironment() {
        assertEquals(143, config.getSplineReticulationCount());
    }

    @Test
    public void retrievesEndpointsFromSystemProperty() {
        assertArrayEquals(new String[] { "foo17", "foo18", "foo19"}, config.getEndpoints());
    }

    @Test
    public void retrievesFooBarsFromSystemProperty() {
        assertArrayEquals(new MyEnum[] { MyEnum.FOO, MyEnum.BAR }, config.getFooBars());
    }

    @Test
    public void retrievesDirigibleEnabledFromOverridingSystemProperty() {
        assertTrue(config.isDirigibleEnabled());
    }

    @Test
    public void retrievesDataPointsFromOverridingSystemProperty() {
        assertArrayEquals(new MyClass[]{new MyClass("0"), new MyClass("1"), new MyClass("2")},
                config.dataPoints());
    }

    @Test
    public void retrievesMoreDataPointsFromOverridingSystemProperty() {
        assertArrayEquals(new MyCustomClass[] { new MyCustomClass("x", 7), new MyCustomClass("y", 18), new MyCustomClass("z", 41) },
                config.moreDataPoints());
    }

    @Test
    public void ignoresDefaultValueWhenSystemPropertyIsAvailable() {
        assertEquals(new MyClass("foo-bar-63"), config.ignoresTheDefault());
    }

    @Test
    public void usesDefaultValueWhenNoSystemPropertyOrEnvironmentVariableIsAvailable() {
        assertEquals(new MyCustomClass("j", 47), config.usesTheDefault());
    }
}
