package com.statemachinesystems.envy;

import com.statemachinesystems.envy.common.StubConfigSource;
import com.statemachinesystems.envy.parsers.IntegerValueParser;
import com.statemachinesystems.envy.parsers.ObjectAsStringValueParser;
import com.statemachinesystems.envy.parsers.StringValueParser;
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

        @Optional
        Integer optionalNonNull();

        @Optional
        Integer optionalNull();

        Object methodWithObjectReturnType();
    }

    @SuppressWarnings("unused")
    public interface BadConfigCombiningOptionalWithPrimitive {
        @Optional
        int notOptional();
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
                .add("OPTIONAL_NON_NULL", "5")
                .add("METHOD_WITH_OBJECT_RETURN_TYPE", "bar")
                .add("INT_IN_SUB_INTERFACE", "37")
                .add("INT_IN_SUB_SUB_INTERFACE", "98")
                .add("ANOTHER_STRING", "baz");

        valueParserFactory = new ValueParserFactory(new StringValueParser(), new IntegerValueParser(),
                new ObjectAsStringValueParser());

        configExtractor = new ConfigExtractor(valueParserFactory, configSource);
    }

    @Test
    public void retrievesStringProperty() throws Throwable {
        assertEquals("foo", getValue("getAString"));
    }

    @Test
    public void retrievesBoxedIntegerProperty() throws Throwable {
        assertEquals(10, getValue("getABoxedInteger"));
    }

    @Test
    public void retrievesPrimitiveIntegerProperty() throws Throwable {
        assertEquals(15, getValue("getAPrimitiveInteger"));
    }

    @Test
    public void retrievesArrayOfBoxedInteger() throws Throwable {
        assertArrayEquals(new Integer[]{ 7 }, (Integer[]) getValue("getAnArrayOfBoxedIntegers"));
    }

    @Test
    public void retrievesArrayOfPrimitiveIntegers() throws Throwable {
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) getValue("getAnArrayOfPrimitiveIntegers"));
    }

    @Test
    public void retrievesDefaultValue() throws Throwable {
        assertEquals("default value", getValue("defaultedString"));
    }

    @Test
    public void retrievesValueWithCustomName() throws Throwable {
        assertEquals("bar", getValue("stringWithCustomName"));
    }

    @Test
    public void retrievesOptionalNonNullValue() throws Throwable {
        assertEquals(5, getValue("optionalNonNull"));
    }

    @Test
    public void retrievesOptionalNullValue() throws Throwable {
        assertNull(getValue("optionalNull"));
    }

    @Test(expected = MissingParameterValueException.class)
    public void rejectsMissingParameter() {
        ConfigSource emptyConfigSource = new StubConfigSource();
        ConfigExtractor configExtractor = new ConfigExtractor(valueParserFactory, emptyConfigSource);
        configExtractor.extractValuesByMethodName(ExampleConfig.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsUnsupportedType() {
        ValueParserFactory emptyValueParserFactory = new ValueParserFactory();
        ConfigExtractor configExtractor = new ConfigExtractor(emptyValueParserFactory, configSource);
        configExtractor.extractValuesByMethodName(ExampleConfig.class);
    }

    @Test(expected = MissingParameterValueException.class)
    public void rejectsOptionalAnnotationWithPrimitiveReturnType() {
        configExtractor.extractValuesByMethodName(BadConfigCombiningOptionalWithPrimitive.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void overriddenObjectMethodsAreRejected() {
        StubConfigSource configSource = new StubConfigSource().add("to.string", "bad");
        ConfigExtractor configExtractor = new ConfigExtractor(valueParserFactory, configSource);
        configExtractor.extractValuesByMethodName(BadConfigWithOverriddenObjectMethod.class);
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
        configExtractor.extractValuesByMethodName(BadConfigWithVoidReturnType.class);
    }

    private Object getValue(String methodName) {
        return configExtractor.extractValuesByMethodName(ExampleConfig.class).get(methodName);
    }
}
