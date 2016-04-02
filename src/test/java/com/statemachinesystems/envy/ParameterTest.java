package com.statemachinesystems.envy;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ParameterTest {

    @Test
    public void createsParameterFromBeanPropertyMethodName() {
        assertThat(Parameter.fromMethodName("getFoo"), equalTo(new Parameter("FOO")));
    }

    @Test
    public void createsParameterFromBooleanBeanPropertyMethodName() {
        assertThat(Parameter.fromMethodName("isBar"), equalTo(new Parameter("BAR")));
    }

    @Test
    public void createsParameterFromSingleWordMethodName() {
        assertThat(Parameter.fromMethodName("fizz"), equalTo(new Parameter("FIZZ")));
    }

    @Test
    public void createsParameterFromMultipleWordMethodName() {
        assertThat(Parameter.fromMethodName("fizzBuzz"), equalTo(new Parameter("FIZZ_BUZZ")));
    }

    @Test
    public void createsParameterFromDeceptiveNonBeanPropertyMethodName() {
        assertThat(Parameter.fromMethodName("gettingTheThing"),
                equalTo(new Parameter("GETTING_THE_THING")));
    }

    @Test
    public void createsParameterFromDeceptiveBooleanNonBeanPropertyMethodName() {
        assertThat(Parameter.fromMethodName("islandInTheSun"),
                equalTo(new Parameter("ISLAND_IN_THE_SUN")));
    }

    @Test
    public void createsParameterFromInitialCapsMethodName() {
        assertThat(Parameter.fromMethodName("FooBar"), equalTo(new Parameter("FOO_BAR")));
    }

    @Test
    public void createsParameterFromAllCapsMethodName() {
        assertThat(Parameter.fromMethodName("FOOBAR"), equalTo(new Parameter("FOOBAR")));
    }

    @Test
    public void createsParameterFromBareBeanPropertyPrefix_is() {
        assertThat(Parameter.fromMethodName("is"), equalTo(new Parameter("IS")));
    }

    @Test
    public void createsParameterFromBareBeanPropertyPrefix_get() {
        assertThat(Parameter.fromMethodName("get"), equalTo(new Parameter("GET")));
    }

    @Test
    public void createsParameterFromTrickyCapsMethodName() {
        assertThat(Parameter.fromMethodName("URLOfTheThing"), equalTo(new Parameter("url.of.the.thing")));
    }

    @Test
    public void createsParameterFromTrickyCapsMethodNameWithDigits() {
        assertThat(Parameter.fromMethodName("HTTP11Proxy"), equalTo(new Parameter("http11.proxy")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyMethodName() {
        Parameter.fromMethodName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMethodNameWithLeadingDollar() {
        Parameter.fromMethodName("$foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMethodNameWithTrailingDollar() {
        Parameter.fromMethodName("foo$");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMethodNameWithDollarAfterInitialCaps() {
        Parameter.fromMethodName("F$");
    }

    @Test
    public void convertsSingleWordSystemPropertyStyleNameToEnvironmentVariableStyle()  {
        assertThat(new Parameter("foo").asEnvironmentVariableName(), equalTo("FOO"));
    }

    @Test
    public void convertsSeparatedSystemPropertyStyleNameToEnvironmentVariableStyle()  {
        assertThat(new Parameter("foo.bar").asEnvironmentVariableName(), equalTo("FOO_BAR"));
    }

    @Test
    public void keepsSingleWordEnvironmentVariableStyle() {
        assertThat(new Parameter("FOO").asEnvironmentVariableName(), equalTo("FOO"));
    }

    @Test
    public void keepsSeparatedEnvironmentVariableStyle() {
        assertThat(new Parameter("FOO_BAR").asEnvironmentVariableName(), equalTo("FOO_BAR"));
    }

    @Test
    public void convertsSingleWordEnvironmentVariableStyleNameToSystemPropertyStyle() {
        assertThat(new Parameter("FOO").asSystemPropertyName(), equalTo("foo"));
    }

    @Test
    public void convertsSeparatedEnvironmentVariableStyleNameToSystemPropertyStyle() {
        assertThat(new Parameter("FOO_BAR").asSystemPropertyName(), equalTo("foo.bar"));
    }

    @Test
    public void keepsSingleWordSystemPropertyStyle() {
        assertThat(new Parameter("foo").asSystemPropertyName(), equalTo("foo"));
    }

    @Test
    public void keepsSeparatedSystemPropertyStyle() {
        assertThat(new Parameter("foo.bar").asSystemPropertyName(), equalTo("foo.bar"));
    }

    @Test
    public void sameNamesAreEqual() {
        assertThat(new Parameter("foo"), equalTo(new Parameter("foo")));
    }

    @Test
    public void equivalentNamesWithDifferentCaseAreEqual() {
        assertThat(new Parameter("foo"), equalTo(new Parameter("FOO")));
    }

    @Test
    public void differentNamesAreNotEqual() {
        assertThat(new Parameter("foo"), not(equalTo(new Parameter("bar"))));
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullName() {
        new Parameter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyName() {
        new Parameter("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankName() {
        new Parameter("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsIllegalCharacterInName() {
        new Parameter("!foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsWhitespaceAroundName() {
        new Parameter(" \tfoo\n ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsWhitespaceInsideName() {
        new Parameter("foo \tbar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRepeatedDotSeparatorsInName() {
        new Parameter("foo..bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRepeatedUnderscoreSeparatorsInName() {
        new Parameter("FOO__BAR");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsLeadingDotInName() {
        new Parameter(".bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsLeadingUnderscoreInName() {
        new Parameter("_BAR");
    }

    @Test
    public void joinsTwoParameters() {
        Parameter prefix = new Parameter("prefix");
        Parameter suffix = new Parameter("suffix");
        assertThat(prefix.join(suffix), equalTo(new Parameter("prefix.suffix")));
    }
}
