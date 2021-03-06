package com.statemachinesystems.envy;

import java.util.regex.Pattern;

/**
 * An identifier representing either a JVM system property in
 * lower.case.dotted format or an environment variable in UPPER_CASE_UNDERSCORED format.
 */
public class Parameter {

    private static final String unicodeAlphaNumeric = "[\\p{L}\\d]+";
    private static final String separator = "[._]";

    private static final Pattern validNamePattern =
            Pattern.compile(String.format("(%s)(%s%s)*", unicodeAlphaNumeric, separator, unicodeAlphaNumeric));

    public static Parameter fromMethodName(String name) {
        int prefixLength = getBeanPropertyPrefixLength(name);

        return fromPropertyName(name.substring(prefixLength));
    }

    private static Parameter fromPropertyName(String name) {
        PropertyNameParser parser = new PropertyNameParser(name);

        StringBuilder buf = new StringBuilder();
        for (String part : parser.parse()) {
            if (buf.length() > 0) {
                buf.append('_');
            }
            buf.append(part.toUpperCase());
        }
        return new Parameter(buf.toString());
    }

    private static int getBeanPropertyPrefixLength(String name) {
        if (hasBeanPropertyPrefix(name, "get")) {
            return "get".length();
        } else if (hasBeanPropertyPrefix(name, "is")) {
            return "is".length();
        } else {
            return 0;
        }
    }

    private static boolean hasBeanPropertyPrefix(String name, String prefix) {
        return name.length() > prefix.length()
                && name.startsWith(prefix)
                && Character.isUpperCase(name.charAt(prefix.length()));
    }

    private final String name;

    public Parameter(String name) {
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (! validNamePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name format: " + name);
        }
        this.name = name;
    }

    public String asEnvironmentVariableName() {
        return name.toUpperCase().replaceAll("\\.", "_");
    }

    public String asSystemPropertyName() {
        return name.toLowerCase().replaceAll("_", ".");
    }

    public Parameter join(Parameter other) {
        return new Parameter(
                String.format("%s_%s", this.asEnvironmentVariableName(), other.asEnvironmentVariableName()));
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
