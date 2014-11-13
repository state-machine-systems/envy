package com.statemachinesystems.envy.example;

public class MyCustomClass {

    private final String foo;
    private final int bar;

    public MyCustomClass(String foo, int bar) {
        this.foo = foo;
        this.bar = bar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyCustomClass that = (MyCustomClass) o;

        if (bar != that.bar) return false;
        if (!foo.equals(that.foo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = foo.hashCode();
        result = 31 * result + bar;
        return result;
    }
}
