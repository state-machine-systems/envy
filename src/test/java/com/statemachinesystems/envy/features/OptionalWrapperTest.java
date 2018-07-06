package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Default;
import com.statemachinesystems.envy.Nullable;
import com.statemachinesystems.envy.UnsupportedTypeException;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;
import scala.Option;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class OptionalWrapperTest extends FeatureTest {

    interface Java8Simple {
        Optional<String> foo();
    }

    interface GuavaSimple {
        com.google.common.base.Optional<String> foo();
    }

    interface ScalaSimple {
        Option<String> foo();
    }

    interface Java8WithDefault {
        @Default("baz")
        Optional<String> foo();
    }

    interface GuavaWithDefault {
        @Default("baz")
        com.google.common.base.Optional<String> foo();
    }

    interface ScalaWithDefault {
        @Default("baz")
        Option<String> foo();
    }

    interface Java8WithAnnotation {
        @Nullable
        Optional<Integer> foo();
    }

    interface Java8WithArray {
        Optional<Integer[]> foo();
    }

    interface Java8WithPrimitiveArray {
        Optional<int[]> foo();
    }

    interface Java8WithNested {
        interface Nested {
            String bar();
        }

        Optional<Nested> foo();
    }

    interface Java8WithNestedDefaults {
        interface Nested {
            @Default("default bar")
            String bar();

            @Default("default baz")
            String baz();
        }

        Optional<Nested> foo();
    }

    interface Java8WithArrayOfOptional {
        Optional<String>[] foo();
    }

    interface Java8WithOptionalOptional {
        Optional<Optional<String>> foo();
    }

    interface Java8WithWildcard {
        Optional<? extends CharSequence> foo();
    }

    interface Java8WithArrayOfGenericType {
        <T> Optional<T[]> foo();
    }

    @Test
    public void wrapsProvidedValueWithJava8Optional() {
        Java8Simple java8 = envy(configSource().add("foo", "bar")).proxy(Java8Simple.class);
        assertThat(java8.foo(), equalTo(Optional.of("bar")));
    }

    @Test
    public void wrapsMissingValueWithJava8Optional() {
        Java8Simple java8 = envy().proxy(Java8Simple.class);
        assertThat(java8.foo(), equalTo(Optional.<String>empty()));
    }

    @Test
    public void wrapsDefaultValueWithJava8Optional() {
        Java8WithDefault java8 = envy().proxy(Java8WithDefault.class);
        assertThat(java8.foo(), equalTo(Optional.of("baz")));
    }

    @Test
    public void wrapsProvidedValueWithGuavaOptional() {
        GuavaSimple guava = envy(configSource().add("foo", "bar")).proxy(GuavaSimple.class);
        assertThat(guava.foo(), equalTo(com.google.common.base.Optional.of("bar")));
    }

    @Test
    public void wrapsMissingValueWithGuavaOptional() {
        GuavaSimple guava = envy().proxy(GuavaSimple.class);
        assertThat(guava.foo(), equalTo(com.google.common.base.Optional.<String>absent()));
    }

    @Test
    public void wrapsDefaultValueWithGuavaOptional() {
        GuavaWithDefault guava = envy().proxy(GuavaWithDefault.class);
        assertThat(guava.foo(), equalTo(com.google.common.base.Optional.of("baz")));
    }

    @Test
    public void wrapsProvidedValueWithScalaOption() {
        ScalaSimple scala = envy(configSource().add("foo", "bar")).proxy(ScalaSimple.class);
        assertThat(scala.foo(), equalTo(Option.apply("bar")));
    }

    @Test
    public void wrapsMissingValueWithScalaOption() {
        ScalaSimple scala = envy().proxy(ScalaSimple.class);
        assertThat(scala.foo(), equalTo(Option.<String>empty()));
    }

    @Test
    public void wrapsDefaultValueWithScalaOption() {
        ScalaWithDefault scala = envy().proxy(ScalaWithDefault.class);
        assertThat(scala.foo(), equalTo(Option.apply("baz")));
    }

    @Test
    public void nullableAnnotationHasNoEffectForPresentValue() {
        Java8WithAnnotation java8 = envy(configSource().add("foo", "1")).proxy(Java8WithAnnotation.class);
        assertThat(java8.foo(), equalTo(Optional.of(1)));
    }

    @Test
    public void nullableAnnotationHasNoEffectForAbsentValue() {
        Java8WithAnnotation java8 = envy().proxy(Java8WithAnnotation.class);
        assertThat(java8.foo(), equalTo(Optional.<Integer>empty()));
    }

    @Test
    public void wrapsArray() {
        Java8WithArray java8 = envy(configSource().add("foo", "1,2,3")).proxy(Java8WithArray.class);
        assertArrayEquals(new Integer[]{1, 2, 3}, java8.foo().get());
    }

    @Test
    public void wrapsPrimitiveArray() {
        Java8WithPrimitiveArray java8 = envy(configSource().add("foo", "1,2,3")).proxy(Java8WithPrimitiveArray.class);
        assertArrayEquals(new int[]{1, 2, 3}, java8.foo().get());

    }

    @Test
    public void wrapsProvidedNestedValue() {
        Java8WithNested java8 = envy(configSource().add("foo.bar", "baz")).proxy(Java8WithNested.class);
        assertThat(java8.foo().get().bar(), equalTo("baz"));
    }

    @Test
    public void wrapsMissingNestedValue() {
        Java8WithNested java8 = envy().proxy(Java8WithNested.class);
        assertThat(java8.foo(), equalTo(Optional.<Java8WithNested.Nested>empty()));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsArrayOfOptionalValues() {
        envy().proxy(Java8WithArrayOfOptional.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsOptionalOptional() {
        envy().proxy(Java8WithOptionalOptional.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsWildcard() {
        envy().proxy(Java8WithWildcard.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsGenericArray() {
        envy().proxy(Java8WithArrayOfGenericType.class);
    }

    @Test
    public void optionalNestedConfigWithDefaultsAndAllValuesProvidedIsPopulated() {
        ConfigSource configSource = configSource().add("foo.bar", "x").add("foo.baz", "y");
        Java8WithNestedDefaults config = envy(configSource).proxy(Java8WithNestedDefaults.class);
        assertThat(config.foo().get().bar(), equalTo("x"));
        assertThat(config.foo().get().baz(), equalTo("y"));
    }

    @Test
    public void optionalNestedConfigWithDefaultsAndNoValuesProvidedIsPopulated() {
        Java8WithNestedDefaults config = envy().proxy(Java8WithNestedDefaults.class);
        assertThat(config.foo().get().bar(), equalTo("default bar"));
        assertThat(config.foo().get().baz(), equalTo("default baz"));
    }

    @Test
    public void optionalNestedConfigWithDefaultsAndSomeValuesProvidedIsPopulated() {
        ConfigSource configSource = configSource().add("foo.bar", "x");
        Java8WithNestedDefaults config = envy(configSource).proxy(Java8WithNestedDefaults.class);
        assertThat(config.foo().get().bar(), equalTo("x"));
        assertThat(config.foo().get().baz(), equalTo("default baz"));
    }
}
