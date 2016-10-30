package com.statemachinesystems.envy.features;

import com.google.common.base.Optional;
import com.statemachinesystems.envy.Nullable;
import com.statemachinesystems.envy.Sensitive;
import com.statemachinesystems.envy.SensitiveValueWrapper;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SensitiveValuesTest extends FeatureTest {

    interface A {
        @Sensitive
        String x();
    }

    interface B extends A {}

    interface C extends A {
        String x();
    }

    interface WithNullable {
        @Nullable
        @Sensitive
        String x();
    }

    interface WithOptional {
        @Sensitive
        Optional<String> x();
    }

    interface WithNested {
        interface Inner { String x(); }

        @Sensitive
        Inner inner();
    }

    interface WithNestedSensitive {
        interface Inner {
            @Sensitive
            String x();

            String y();
        }

        Inner inner();
    }

    @Test
    public void excludesSensitiveFieldFromToString() {
        A config = envy(configSource().add("x", "1")).proxy(A.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
    }

    @Test
    public void includesMissingNullableValueInToString() {
        WithNullable config = envy(configSource()).proxy(WithNullable.class);
        assertThat(config.toString(), not(containsString(SensitiveValueWrapper.MASKED_VALUE)));
        assertThat(config.toString(), containsString("null"));
    }

    @Test
    public void excludesProvidedNullableValueInToString() {
        WithNullable config = envy(configSource().add("x", "1")).proxy(WithNullable.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
    }

    @Test
    public void includesMissingOptionalValueInToString() {
        WithOptional config = envy(configSource()).proxy(WithOptional.class);
        assertThat(config.toString(), not(containsString(SensitiveValueWrapper.MASKED_VALUE)));
        assertThat(config.toString(), containsString("absent"));
    }

    @Test
    public void excludesProvidedOptionalValueInToString() {
        WithOptional config = envy(configSource().add("x", "1")).proxy(WithOptional.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
    }

    @Test
    public void retainsSensitiveAnnotationInSubInterface() {
        B config = envy(configSource().add("x", "1")).proxy(B.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
    }

    @Test
    public void shouldOverrideSensitiveAnnotationInSubInterface() {
        C config = envy(configSource().add("x", "1")).proxy(C.class);
        assertThat(config.toString(), not(containsString(SensitiveValueWrapper.MASKED_VALUE)));
        assertThat(config.toString(), containsString("1"));
    }

    @Test
    public void excludesEntireNestedValueInToString() {
        WithNested config = envy(configSource().add("inner.x", "1")).proxy(WithNested.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
        assertThat(config.toString(), not(containsString("x")));
    }

    @Test
    public void excludesSensitivePropertiesOfNestedValueInToString() {
        WithNestedSensitive config = envy(configSource().add("inner.x", "1").add("inner.y", "2")).proxy(WithNestedSensitive.class);
        assertThat(config.toString(), containsString(SensitiveValueWrapper.MASKED_VALUE));
        assertThat(config.toString(), not(containsString("1")));
        assertThat(config.toString(), containsString("2"));
    }
}
