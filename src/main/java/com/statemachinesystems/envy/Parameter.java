package com.statemachinesystems.envy;

import java.beans.PropertyDescriptor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parameter {

    private static final String unicodeAlphaNumeric = "[\\p{L}\\d]+";
    private static final String separator = "[._]";

    private static final Pattern validNamePattern =
            Pattern.compile(String.format("(%s)(%s%s)*", unicodeAlphaNumeric, separator, unicodeAlphaNumeric));

    // TODO digits? dollars? underscores?
    private static final Pattern beanNamePattern =
            Pattern.compile("\\p{javaLowerCase}+"
                    + "|\\p{javaUpperCase}{2,}\\p{javaLowerCase}*"
                    + "|\\p{javaUpperCase}\\p{javaLowerCase}+");

    public static Parameter fromProperty(PropertyDescriptor prop) {
        Matcher matcher = beanNamePattern.matcher(prop.getName());
        StringBuilder buf = new StringBuilder();
        while (matcher.find()) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(matcher.group().toLowerCase());
        }
        return new Parameter(buf.toString());
    }

    private final String name;

    public Parameter(String name) {
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (! validNamePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name format");
        }
        this.name = name;
    }

    public String asEnvironmentVariableName() {
        return name.toUpperCase().replaceAll("\\.", "_");
    }

    public String asSystemPropertyName() {
        return name.toLowerCase().replaceAll("_", ".");
    }

    @Override
    public String toString() {
        return this.asEnvironmentVariableName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter that = (Parameter) o;

        return this.asEnvironmentVariableName().equals(that.asEnvironmentVariableName());
    }

    @Override
    public int hashCode() {
        return this.asEnvironmentVariableName().hashCode();
    }
}
