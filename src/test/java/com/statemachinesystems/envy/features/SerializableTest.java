package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.common.FeatureTest;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SerializableTest extends FeatureTest {

    @SuppressWarnings("unused")
    public interface Config {
        int foo();
        String bar();
    }

    @Test
    public void proxyInstancesAreSerializable() throws IOException, ClassNotFoundException {
        Config config = envy(configSource().add("foo", "1").add("bar", "bar")).proxy(Config.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(config);

        Object deserialized = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();
        assertThat(deserialized, instanceOf(Config.class));
        assertThat(deserialized, is(config));
    }
}
