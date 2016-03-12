package com.statemachinesystems.envy.features;

import com.statemachinesystems.envy.common.FeatureTest;
import com.statemachinesystems.envy.common.DummyConfigSource;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InheritanceTest extends FeatureTest {

    public interface A {
        String a1();
        Integer a2();
    }

    public interface B extends A {
        String a1();
        int b();
    }

    public interface C {
        String c();
    }

    public interface D extends B, C {
        int d();
    }

    @Override
    protected DummyConfigSource configSource() {
        return super.configSource()
                .add("a1", "a string value")
                .add("a2", "123")
                .add("b", "456")
                .add("c", "another string value")
                .add("d", "789");
    }

    @Test
    public void callMethodDeclaredInChild() throws Throwable {
        B config = envy().proxy(B.class);
        assertThat(config.b(), is(456));
    }

    @Test
    public void callMethodDeclaredInParent() throws Throwable {
        B config = envy().proxy(B.class);
        assertThat(config.a2(), is(123));
    }

    @Test
    public void callMethodDeclaredInBothParentAndChild() throws Throwable {
        A parentConfig = envy().proxy(A.class);
        B childConfig = envy().proxy(B.class);

        String returnValueUsingSuperMethod = parentConfig.a1();
        assertThat(returnValueUsingSuperMethod, is("a string value"));

        String returnValueUsingSubMethod = childConfig.a1();
        assertThat(returnValueUsingSubMethod, is("a string value"));
    }

    @Test
    public void callMethodDeclaredInGrandchild() throws Throwable {
        D grandchild = envy().proxy(D.class);
        assertThat(grandchild.d(), is(789));
    }

    @Test
    public void callMethodDeclaredInSiblingOfParent() throws Throwable {
        D child = envy().proxy(D.class);
        assertThat(child.c(), is("another string value"));
    }
}
