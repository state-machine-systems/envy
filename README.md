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

Also, you don't have to use bean-style method names - the following version would work in exactly the same way:

    interface MyConfig {
        String apiKey();
        String apiSecret();
    }

### Getting started

Envy's in the Maven Central repo, so just add the
[latest version](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.statemachinesystems%22%20AND%20a%3A%22envy%22)
as a dependency in your Maven/SBT/Gradle/whatever build.

### Default values

Envy treats all parameters as mandatory - nulls are bad.
Instead, you can provide a default value for optional parameters:

    import com.statemachinesystems.envy.Default;

    interface ServerConfig {
        @Default("80")
        int getHttpPort();
    }

### Optional values

Sometimes a parameter type has no meaningful default value, or you need to test for its absence. Nulls are still bad,
but you can have them if you really need them:

    import com.statemachinesystems.envy.Optional;

    interface FooConfig {
        @Optional
        URL getOptionalUrl();
    }

### Vanity naming

Long and/or awkward names can be overridden using the `@Name` annotation:

    import com.statemachinesystems.envy.Name;

    interface BarConfig {
        @Name("com.foo.extremely.long.property.name.for.thing")
        String getThing();
    }

To apply a prefix to all names in a configuration interface, use the `@Prefix` annotation:

    import com.statemachinesystems.envy.Prefix;

    @Prefix("baz.config")
    interface BazConfig {
        /**
         * Configured by BAZ_CONFIG_HTTP_PORT
         */
        int getHttpPort();
    }

### Inheritance

Interface inheritance is supported, so you can factor out common parameters in more complex configurations.

Annotations on methods (`@Name`, `@Optional` and `@Default`) are inherited,
but can be overridden (or removed entirely) by redeclaring the method in a sub-interface. `@Prefix` annotations
are *not* inherited.


### Nesting

Configuration interfaces can be nested, which allows reuse of repeated structures. Here's an example using both
inheritance and nesting:

    interface Credentials {
        String username();
        String password();
    }

    interface ConnectionConfig extends Credentials {
        java.net.InetSocketAddress address();
    }

    interface AppConfig {
        /**
         * Configured by DATABASE_ADDRESS, DATABASE_USERNAME and DATABASE_PASSWORD
         */
        ConnectionConfig database();

        /**
         * Configured by MESSAGE_BROKER_ADDRESS, MESSAGE_BROKER_USERNAME and MESSAGE_BROKER_PASSWORD
         */
        ConnectionConfig messageBroker();
    }

### Supported data types

Envy will do the following type conversions for you:

* Strings (no conversion needed)
* Numbers (`int`/`Integer`, `long`/`Long`, `byte`/`Byte`, `short`/`Short`, `float`/`Float`, `double`/`Double`, `java.math.BigDecimal`, `java.math.BigInteger`)
* Booleans (true/false, yes/no, y/n, on/off)
* Characters (`char`/`Character`)
* Enums
* Arrays, comma-separated
* Anything with a constructor that takes a single `String` argument
* `java.io.File`
* `java.lang.Class`
* `java.net.URL`, `java.net.URI`
* `java.net.InetAddress`, `Inet4Address`, `Inet6Address`, `InetSocketAddress`
* `java.util.regex.Pattern`
* `java.util.UUID`

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


&copy; 2016 State Machine Systems Ltd. [Apache Licence, Version 2.0]( http://www.apache.org/licenses/LICENSE-2.0)
