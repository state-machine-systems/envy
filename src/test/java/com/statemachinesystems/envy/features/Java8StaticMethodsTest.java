package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Java8StaticMethodsTest extends FeatureTest {

    interface Java8InterfaceWithStaticMethods {
        static void staticMethod() {}
        String instanceMethod();
    }

    @Test
    public void staticMethodsAreIgnored() {
        Java8InterfaceWithStaticMethods proxy = envy(configSource().add("instance.method", "xxx"))
                .proxy(Java8InterfaceWithStaticMethods.class);

        assertThat(proxy.instanceMethod(), is("xxx"));
    }
}
