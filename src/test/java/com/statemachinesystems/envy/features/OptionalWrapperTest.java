package com.statemachinesystems.envy.features;

import com.google.common.base.Optional;
import com.statemachinesystems.envy.Default;
import com.statemachinesystems.envy.Nullable;
import com.statemachinesystems.envy.UnsupportedTypeException;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;
import scala.Option;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class OptionalWrapperTest extends FeatureTest {

    interface GuavaSimple {
        Optional<String> foo();
    }

    interface ScalaSimple {
        Option<String> foo();
    }

    interface GuavaWithDefault {
        @Default("baz")
        Optional<String> foo();
    }

    interface ScalaWithDefault {
        @Default("baz")
        Option<String> foo();
    }

    interface GuavaWithAnnotation {
        @Nullable
        Optional<Integer> foo();
    }

    interface GuavaWithArray {
        Optional<Integer[]> foo();
    }

    interface GuavaWithPrimitiveArray {
        Optional<int[]> foo();
    }

    interface GuavaWithNested {
        interface Nested {
            String bar();
        }
        Optional<Nested> foo();
    }

    interface GuavaWithArrayOfOptional {
        Optional<String>[] foo();
    }

    interface GuavaWithOptionalOptional {
        Optional<Optional<String>> foo();
    }

    interface GuavaWithWildcard {
        Optional<? extends CharSequence> foo();
    }

    interface GuavaWithArrayOfGenericType {
        <T> Optional<T[]> foo();
    }

    @Test
    public void wrapsProvidedValueWithGuavaOptional() {
        GuavaSimple guava = envy(configSource().add("foo", "bar")).proxy(GuavaSimple.class);
        assertThat(guava.foo(), equalTo(Optional.of("bar")));
    }

    @Test
    public void wrapsMissingValueWithGuavaOptional() {
        GuavaSimple guava = envy().proxy(GuavaSimple.class);
        assertThat(guava.foo(), equalTo(Optional.<String>absent()));
    }

    @Test
    public void wrapsDefaultValueWithGuavaOptional() {
        GuavaWithDefault guava = envy().proxy(GuavaWithDefault.class);
        assertThat(guava.foo(), equalTo(Optional.of("baz")));
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
        GuavaWithAnnotation guava = envy(configSource().add("foo", "1")).proxy(GuavaWithAnnotation.class);
        assertThat(guava.foo(), equalTo(Optional.of(1)));
    }

    @Test
    public void nullableAnnotationHasNoEffectForAbsentValue() {
        GuavaWithAnnotation guava = envy().proxy(GuavaWithAnnotation.class);
        assertThat(guava.foo(), equalTo(Optional.<Integer>absent()));
    }

    @Test
    public void wrapsArray() {
        GuavaWithArray guava = envy(configSource().add("foo", "1,2,3")).proxy(GuavaWithArray.class);
        assertArrayEquals(new Integer[] { 1, 2, 3 }, guava.foo().get());
    }

    @Test
    public void wrapsPrimitiveArray() {
        GuavaWithPrimitiveArray guava = envy(configSource().add("foo", "1,2,3")).proxy(GuavaWithPrimitiveArray.class);
        assertArrayEquals(new int[] { 1, 2, 3 }, guava.foo().get());

    }

    @Test
    public void wrapsProvidedNestedValue() {
        GuavaWithNested guava = envy(configSource().add("foo.bar", "baz")).proxy(GuavaWithNested.class);
        assertThat(guava.foo().get().bar(), equalTo("baz"));
    }

    @Test
    public void wrapsMissingNestedValue() {
        GuavaWithNested guava = envy().proxy(GuavaWithNested.class);
        assertThat(guava.foo(), equalTo(Optional.<GuavaWithNested.Nested>absent()));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsArrayOfOptionalValues() {
        envy().proxy(GuavaWithArrayOfOptional.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsOptionalOptional() {
        envy().proxy(GuavaWithOptionalOptional.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsWildcard() {
        envy().proxy(GuavaWithWildcard.class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsGenericArray() {
        envy().proxy(GuavaWithArrayOfGenericType.class);
    }
}
