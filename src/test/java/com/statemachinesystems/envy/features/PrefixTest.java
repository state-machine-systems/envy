package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.Name;
import com.statemachinesystems.envy.Prefix;
import com.statemachinesystems.envy.common.FeatureTest;
import com.statemachinesystems.envy.common.StubConfigSource;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrefixTest extends FeatureTest {

    @Prefix("xxx")
    @SuppressWarnings("unused")
    interface A {
        int inherited();

        @Name("custom.name.inherited")
        int inheritedWithCustomName();

        @Name("xxx")
        int customNameOverridden();

        @Name("xxx")
        int customNameRemoved();
    }

    @Prefix("custom.prefix")
    interface B extends A {

        int plain();

        @Name("custom.name")
        int withCustomName();

        @Name("overridden.custom.name")
        int customNameOverridden();

        int customNameRemoved();
    }

    @Override
    protected StubConfigSource configSource() {
        return super.configSource()
                .add("custom.prefix.plain", "1")
                .add("custom.prefix.custom.name", "2")
                .add("custom.prefix.inherited", "3")
                .add("custom.prefix.custom.name.inherited", "4")
                .add("custom.prefix.overridden.custom.name", "5")
                .add("custom.prefix.custom.name.removed", "6");
    }

    @Test
    public void appliesPrefixToPlainMethod() {
        assertThat(envy().proxy(B.class).plain(), is(1));
    }

    @Test
    public void appliesPrefixToMethodWithCustomName() {
        assertThat(envy().proxy(B.class).withCustomName(), is(2));
    }

    @Test
    public void appliesPrefixToInheritedMethod() {
        assertThat(envy().proxy(B.class).inherited(), is(3));
    }

    @Test
    public void appliesPrefixToInheritedMethodWithCustomName() {
        assertThat(envy().proxy(B.class).inheritedWithCustomName(), is(4));
    }

    @Test
    public void appliesPrefixToInheritedMethodWithOverriddenCustomName() {
        assertThat(envy().proxy(B.class).customNameOverridden(), is(5));
    }

    @Test
    public void appliesPrefixToInheritedMethodWithCustomNameRemoved() {
        assertThat(envy().proxy(B.class).customNameRemoved(), is(6));
    }
}
