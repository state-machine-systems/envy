package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.Default;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Java8OptionalWrapperTest extends FeatureTest {

    interface Java8Simple {
        Optional<String> foo();
    }

    interface Java8WithDefault {
        @Default("baz")
        Optional<String> foo();
    }

    @Test
    public void wrapsProvidedValueWithJava8Optional() {
        Java8Simple Java8 = envy(configSource().add("foo", "bar")).proxy(Java8Simple.class);
        assertThat(Java8.foo(), equalTo(Optional.of("bar")));
    }

    @Test
    public void wrapsMissingValueWithJava8Optional() {
        Java8Simple Java8 = envy().proxy(Java8Simple.class);
        assertThat(Java8.foo(), equalTo(Optional.<String>empty()));
    }

    @Test
    public void wrapsDefaultValueWithJava8Optional() {
        Java8WithDefault Java8 = envy().proxy(Java8WithDefault.class);
        assertThat(Java8.foo(), equalTo(Optional.of("baz")));
    }
}
