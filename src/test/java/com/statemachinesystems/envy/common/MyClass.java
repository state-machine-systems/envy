package com.statemachinesystems.envy.common;

public class MyClass {
    private final String value;

    public MyClass(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyClass myClass = (MyClass) o;

        if (!value.equals(myClass.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "MyClass{" +
                "value='" + value + '\'' +
                '}';
    }
}