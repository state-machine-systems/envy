package com.statemachinesystems.envy;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ParameterTest {

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
}
