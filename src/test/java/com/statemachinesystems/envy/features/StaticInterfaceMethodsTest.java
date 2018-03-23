package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StaticInterfaceMethodsTest extends FeatureTest {

    interface WithStaticMethod {
        static int foo() {
            return 1;
        }

        int bar();
    }

    @Test
    public void ignoresStaticInterfaceMethods() {
        WithStaticMethod withStaticMethod =
                envy(configSource().add("bar", "2")).proxy(WithStaticMethod.class);
        assertThat(withStaticMethod.bar(), equalTo(2));
    }
}
