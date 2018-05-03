package com.statemachinesystems.envy.features.kotlin

import java.util.Optional

interface Grandparent {
    fun x() = "Grandparent x"
}

interface Parent1 : Grandparent {
    override fun x() = "Parent1 x"
}

interface Parent2 : Grandparent {
    fun y() = "Parent2 y"
}

interface Child1 : Parent1, Parent2

interface Child2 : Parent1, Parent2 {
    override fun x() = "Child2 x"
}

interface WithOptionalWrapper {
    val value get() = Optional.of("default value")
}

interface Nested {
    val foo: String
    val bar: String
}

interface WithNested {
    val nested: Nested
}

interface NestedWithDefaults : Nested {
    override val foo get() = "nested default foo"
    override val bar get() = "nested default bar"
}

interface NestedViaDefaultMethod : WithNested {
    override val nested get() = object : Nested {
        override val foo = "default foo"
        override val bar = "default bar"
    }
}

interface NestedUsingDefaultMethodsOnValue : WithNested {
    override val nested: NestedWithDefaults
}

interface NestedOverridingDefaultMethodOnValue : WithNested {
    override val nested get () = object : NestedWithDefaults {
        override val bar = "overridden bar"
    }
}
