package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Nullable;
import com.statemachinesystems.envy.Sensitive;
import com.statemachinesystems.envy.common.FeatureTest;
import com.statemachinesystems.envy.values.SensitiveValue;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SensitiveValuesTest extends FeatureTest {

    interface Credentials {
        String username();

        @Nullable
        @Sensitive
        String password();
    }

    interface Nested {
        Credentials credentials();
    }

    interface NestedSensitive {
        @Nullable
        @Sensitive
        Credentials credentials();
    }

    interface OptionalSensitive {
        @Sensitive
        Optional<Integer> secretNumber();
    }

    interface NestedOptional {
        @Sensitive
        Optional<Credentials> credentials();
    }

    interface Sub1 extends Credentials {
        String type();
    }

    interface Sub2 extends Credentials {
        @Override
        String password();
    }

    @Test
    public void masksSensitiveValues() {
        ConfigSource configSource = configSource()
                .add("username", "scott")
                .add("password", "tiger");
        Credentials config = envy(configSource).proxy(Credentials.class);
        assertThat(config.toString(), not(containsString("tiger")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void doesNotMaskMissingValues() {
        ConfigSource configSource = configSource()
                .add("username", "scott");
        Credentials config = envy(configSource).proxy(Credentials.class);
        assertThat(config.toString(), containsString("null"));
        assertThat(config.toString(), not(containsString(SensitiveValue.MASKED_VALUE)));
    }

    @Test
    public void masksInNestedConfig() {
        ConfigSource configSource = configSource()
                .add("credentials.username", "scott")
                .add("credentials.password", "tiger");

        Nested config = envy(configSource).proxy(Nested.class);
        assertThat(config.toString(), not(containsString("tiger")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void masksEntireNestedConfig() {
        ConfigSource configSource = configSource()
                .add("credentials.username", "scott")
                .add("credentials.password", "tiger");

        NestedSensitive config = envy(configSource).proxy(NestedSensitive.class);
        assertThat(config.toString(), not(containsString("scott")));
        assertThat(config.toString(), not(containsString("tiger")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void doesNotMaskMissingNestedConfig() {
        NestedSensitive config = envy(configSource()).proxy(NestedSensitive.class);
        assertThat(config.toString(), containsString("null"));
        assertThat(config.toString(), not(containsString(SensitiveValue.MASKED_VALUE)));
    }

    @Test
    public void masksOptionalConfig() {
        ConfigSource configSource = configSource().add("secret.number", "42");
        OptionalSensitive config = envy(configSource).proxy(OptionalSensitive.class);
        assertThat(config.toString(), not(containsString("42")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void doesNotMaskMissingOptionalConfig() {
        OptionalSensitive config = envy(configSource()).proxy(OptionalSensitive.class);
        assertThat(config.toString(), containsString(Optional.empty().toString()));
        assertThat(config.toString(), not(containsString(SensitiveValue.MASKED_VALUE)));
    }

    @Test
    public void masksEntireNestedOptionalConfig() {
        ConfigSource configSource = configSource()
                .add("credentials.username", "scott")
                .add("credentials.password", "tiger");

        NestedOptional config = envy(configSource).proxy(NestedOptional.class);
        assertThat(config.toString(), not(containsString("scott")));
        assertThat(config.toString(), not(containsString("tiger")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void doesNotMaskMissingNestedOptionalConfig() {
        NestedOptional config = envy(configSource()).proxy(NestedOptional.class);
        assertThat(config.toString(), containsString(Optional.empty().toString()));
        assertThat(config.toString(), not(containsString(SensitiveValue.MASKED_VALUE)));
    }

    @Test
    public void sensitiveAnnotationIsInherited() {
        ConfigSource configSource = configSource()
                .add("username", "scott")
                .add("password", "tiger")
                .add("type", "admin");
        Sub1 config = envy(configSource).proxy(Sub1.class);
        assertThat(config.toString(), containsString("scott"));
        assertThat(config.toString(), containsString("admin"));
        assertThat(config.toString(), not(containsString("tiger")));
        assertThat(config.toString(), containsString(SensitiveValue.MASKED_VALUE));
    }

    @Test
    public void sensitiveAnnotationCanBeOverridden() {
        ConfigSource configSource = configSource()
                .add("username", "scott")
                .add("password", "tiger");
        Sub2 config = envy(configSource).proxy(Sub2.class);
        assertThat(config.toString(), containsString("scott"));
        assertThat(config.toString(), containsString("tiger"));
        assertThat(config.toString(), not(containsString(SensitiveValue.MASKED_VALUE)));
    }
}
