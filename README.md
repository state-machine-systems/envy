# Envy

## Super simple configuration for Java.

Envy provides easy, uniform access to environment variables and system properties,
which helps you build [twelve-factor apps](http://www.12factor.net/config).

Say you need to access the environment variables `API_KEY` and `API_SECRET` in your program.
First, you define an interface with getter methods named after the parameters you want to bring in:

    interface MyConfig {
        String getApiKey();
        String getApiSecret();
    }

Envy can then instantiate your configuration interface like this:

    import com.statemachinesystems.envy.Envy;
    ...
    MyConfig config = Envy.configure(MyConfig.class);

Now, calling `config.getApiKey()` will return the value of the `API_KEY` environment variable, and
`config.getApiSecret()` will return the value of `API_SECRET`.

This also works for JVM system properties, using a lower-case dotted naming convention,
which would be `api.key` and `api.secret` in the above example. When an environment variable and a
system property are both defined with equivalent names, the system property takes precedence.

### Default values

Envy treats all parameters as mandatory - nulls are bad.
Instead, you can provide a default value for optional parameters:

    import com.statemachinesystems.envy.Default;

    interface ServerConfig {
        @Default("80")
        int getHttpPort();
    }

:x: Not implemented yet!

### Vanity naming

Long and/or awkward names can be overridden using the `@Name` annotation:

    import com.statemachinesystems.envy.Name;

    interface FooConfig {
        @Name("com.foo.extremely.long.property.name.for.thing")
        String getThing();
    }

:x: Not implemented yet!

### Supported data types

Envy will do the following type conversions for you:

* Strings (no conversion needed)
* Numbers (`int`/`Integer`, `long`/`Long`, `byte`/`Byte`, `short`/`Short`, `float`/`Float`, `double`/`Double`, `java.math.BigDecimal`, `java.math.BigInteger`)
* Booleans (true/false, yes/no, y/n, on/off)
* Characters (`char`/`Character`)
* Enums
* Arrays, comma-separated
* Anything with a constructor that takes a single `String` argument
* `java.io.File` :x:
* `java.lang.Class` :x:
* `java.net.URL` :x:, `java.net.URI` :x:
* `java.net.InetAddress` :x:, `Inet4Address`, `Inet6Address`, `InetSocketAddress` :x:
* `java.util.regex.Pattern` :x:
* `java.util.UUID` :x:

### Custom data types

To parse a custom type, implement the `ValueParser` interface:

    import com.statemachinesystems.envy.ValueParser;

    public class MyCustomTypeParser implements ValueParser<MyCustomType> {
        @Override
        public MyCustomType parseValue(String value) {
            ...
        }

        @Override
        public Class<MyCustomType> getValueClass() {
            return MyCustomType.class;
        }
    }

Then, when instantiating your configuration, pass along an instance of your parser like this:

    MyConfig config = Envy.configure(MyConfig.class, new MyCustomTypeParser());

:x: Not implemented yet!

&copy; 2014 State Machine Systems Ltd. [Apache Licence, Version 2.0]( http://www.apache.org/licenses/LICENSE-2.0)
