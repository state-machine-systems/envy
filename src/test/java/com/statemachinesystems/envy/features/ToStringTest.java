package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ToStringTest extends FeatureTest {

    @SuppressWarnings("unused")
    public interface Config {
        int getFoo();
        String getBar();
        int[] getBaz();
    }

    @Test
    public void toStringMethodFormatsUsingMethodNames() {
        ConfigSource configSource = configSource()
                .add("foo", "1")
                .add("bar", "bar")
                .add("baz", "1,2,3");

        Config config = envy(configSource).proxy(Config.class);

        assertThat(config.toString(), startsWith("{"));
        assertThat(config.toString(), endsWith("}"));

        String[] expectedParts = {"getFoo=1", "getBar=bar", "getBaz=[1, 2, 3]"};
        for (String part : expectedParts) {
            assertThat(config.toString(), containsString(part));
        }
    }
}
