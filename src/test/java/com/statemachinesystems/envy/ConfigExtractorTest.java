package com.statemachinesystems.envy;

import com.statemachinesystems.envy.common.StubConfigSource;
import com.statemachinesystems.envy.parsers.IntegerValueParser;
import com.statemachinesystems.envy.parsers.ObjectAsStringValueParser;
import com.statemachinesystems.envy.parsers.StringValueParser;
import com.statemachinesystems.envy.values.ConfigValue;
import com.statemachinesystems.envy.values.ConfigValue.Status;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigExtractorTest {

    @SuppressWarnings("unused")
    public interface ExampleConfig {
        String getAString();
        Integer getABoxedInteger();
        int getAPrimitiveInteger();
        Integer[] getAnArrayOfBoxedIntegers();
        int[] getAnArrayOfPrimitiveIntegers();

        @Default("default value")
        String defaultedString();

        @Name("custom.parameter.name")
        String stringWithCustomName();

        @Nullable
        Integer nullable();

        @Nullable
        Integer missing();

        @SuppressWarnings("deprecation")
        @Optional
        Integer optionalNonNull();

        @SuppressWarnings("deprecation")
        @Optional
        Integer optionalNull();

        Object methodWithObjectReturnType();
    }

    @SuppressWarnings("unused")
    public interface BadConfigCombiningNullableWithPrimitive {
        @Nullable
        int notNullable();
    }

    @SuppressWarnings("unused")
    public interface BadConfigWithOverriddenObjectMethod {
        @Override
        String toString();
    }

    @SuppressWarnings("unused")
    public interface BadConfigWithVoidReturnType {
        void methodWithVoidReturnType();
    }

    private StubConfigSource configSource;
    private ValueParserFactory valueParserFactory;
    private ConfigExtractor configExtractor;

    @Before
    public void setUp() {
        configSource = new StubConfigSource()
                .add("A_STRING", "foo")
                .add("A_BOXED_INTEGER", "10")
                .add("A_PRIMITIVE_INTEGER", "15")
                .add("AN_ARRAY_OF_BOXED_INTEGERS", "7")
                .add("AN_ARRAY_OF_PRIMITIVE_INTEGERS", "1,2,3")
                .add("CUSTOM_PARAMETER_NAME", "bar")
                .add("NULLABLE", "5")
                .add("OPTIONAL_NON_NULL", "6")
                .add("METHOD_WITH_OBJECT_RETURN_TYPE", "bar")
                .add("INT_IN_SUB_INTERFACE", "37")
                .add("INT_IN_SUB_SUB_INTERFACE", "98")
                .add("ANOTHER_STRING", "baz");

        valueParserFactory = new ValueParserFactory(new StringValueParser(), new IntegerValueParser(),
                new ObjectAsStringValueParser());

        configExtractor = new ConfigExtractor(valueParserFactory, configSource);
    }

    @Test
    public void retrievesStringProperty() {
        assertEquals("foo", getValue("getAString", Status.CONFIGURED));
    }

    @Test
    public void retrievesBoxedIntegerProperty() {
        assertEquals(10, getValue("getABoxedInteger", Status.CONFIGURED));
    }

    @Test
    public void retrievesPrimitiveIntegerProperty() {
        assertEquals(15, getValue("getAPrimitiveInteger", Status.CONFIGURED));
    }

    @Test
    public void retrievesArrayOfBoxedInteger() {
        Integer[] value = (Integer[]) getValue("getAnArrayOfBoxedIntegers", Status.CONFIGURED);
        assertArrayEquals(new Integer[]{ 7 }, value);
    }

    @Test
    public void retrievesArrayOfPrimitiveIntegers() {
        int[] value = (int[]) getValue("getAnArrayOfPrimitiveIntegers", Status.CONFIGURED);
        assertArrayEquals(new int[] { 1, 2, 3 }, value);
    }

    @Test
    public void retrievesDefaultValue() {
        assertEquals("default value", getValue("defaultedString", Status.DEFAULTED));
    }

    @Test
    public void retrievesValueWithCustomName() {
        assertEquals("bar", getValue("stringWithCustomName", Status.CONFIGURED));
    }

    @Test
    public void retrievesNullableValue() {
        assertEquals(5, getValue("nullable", Status.CONFIGURED));
    }

    @Test
    public void retrievesNullMissingValue() {
        assertNull(getValue("missing", Status.MISSING));
    }

    @Test
    public void retrievesOptionalNonNullValue() {
        assertEquals(6, getValue("optionalNonNull", Status.CONFIGURED));
    }

    @Test
    public void retrievesOptionalNullValue() {
        assertNull(getValue("optionalNull", Status.MISSING));
    }

    @Test(expected = MissingParameterValueException.class)
    public void rejectsMissingParameter() {
        ConfigSource emptyConfigSource = new StubConfigSource();
        ConfigExtractor configExtractor = new ConfigExtractor(valueParserFactory, emptyConfigSource);
        configExtractor.extractConfigMap(ExampleConfig.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsUnsupportedType() {
        ValueParserFactory emptyValueParserFactory = new ValueParserFactory();
        ConfigExtractor configExtractor = new ConfigExtractor(emptyValueParserFactory, configSource);
        configExtractor.extractConfigMap(ExampleConfig.class);
    }

    @Test(expected = MissingParameterValueException.class)
    public void rejectsNullableAnnotationWithPrimitiveReturnType() {
        configExtractor.extractConfigMap(BadConfigCombiningNullableWithPrimitive.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void overriddenObjectMethodsAreRejected() {
        StubConfigSource configSource = new StubConfigSource().add("to.string", "bad");
        ConfigExtractor configExtractor = new ConfigExtractor(valueParserFactory, configSource);
        configExtractor.extractConfigMap(BadConfigWithOverriddenObjectMethod.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void voidReturnTypesAreRejected() {
        StubConfigSource configSource = new StubConfigSource().add("method.with.void.return.type", "bad");
        ValueParserFactory valueParserFactory = new ValueParserFactory(new ValueParser<Void>() {
            @Override
            public Void parseValue(String value) {
                return null;
            }

            @Override
            public Class<Void> getValueClass() {
                return Void.class;
            }
        });
        ConfigExtractor configExtractor = new ConfigExtractor(valueParserFactory, configSource);
        configExtractor.extractConfigMap(BadConfigWithVoidReturnType.class);
    }

    @Test
    public void mapKeysAreOrderedAlphabetically() {
        String previousKey = "";
        for (String methodName : configExtractor.extractConfigMap(ExampleConfig.class).getMethodNames()) {
            assertTrue(previousKey.compareTo(methodName) <= 0);
            previousKey = methodName;
        }
    }

    private Object getValue(String methodName, Status expectedStatus) {
        ConfigValue configValue = configExtractor.extractConfigMap(ExampleConfig.class).getValue(methodName);
        assertEquals(expectedStatus, configValue.getStatus());
        return configValue.getValue();
    }
}
