package com.statemachinesystems.envy;

import com.statemachinesystems.envy.parsers.*;

import java.util.*;

/**
 * Top-level fa&ccedil;ade for creating configuration objects.
 */
public class Envy {

    /**
     * Create a configuration object from the given interface, optionally with custom value parsers.
     *
     * @param configClass         the configuration interface from which to create the object
     * @param customValueParsers  additional value parsers to be used
     * @param <T>                 the type of the configuration interface
     * @return                    a configuration object that implements the given interface
     */
    public static <T> T configure(Class<T> configClass, ValueParser<?>... customValueParsers) {
        return configure(configClass, new DefaultConfigSource(), customValueParsers);
    }

    /**
     * Create a configuration object from the given interface and {@link com.statemachinesystems.envy.ConfigSource},
     * optionally with custom value parsers.
     *
     * @param configClass         the configuration interface from which to create the object
     * @param configSource        the {@link com.statemachinesystems.envy.ConfigSource} to use
     * @param customValueParsers  additional value parsers to be used
     * @param <T>                 the type of the configuration interface
     * @return                    a configuration object that implements the given interface

     */
    public static <T> T configure(Class<T> configClass, ConfigSource configSource,
            ValueParser<?>... customValueParsers) {

        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>();
        valueParsers.addAll(defaultValueParsers());
        valueParsers.addAll(Arrays.asList(customValueParsers));
        ValueParserFactory valueParserFactory = new ValueParserFactory(valueParsers);

        return new Envy(valueParserFactory, configSource)
                .proxy(configClass);
    }

    /**
     * Provides the default built-in value parsers.
     *
     * @return the default built-in value parsers
     */
    public static List<ValueParser<?>> defaultValueParsers() {
        List<ValueParser<?>> valueParsers = new ArrayList<ValueParser<?>>();

        valueParsers.add(new BigDecimalValueParser());
        valueParsers.add(new BigIntegerValueParser());
        valueParsers.add(new BooleanValueParser());
        valueParsers.add(new ByteValueParser());
        valueParsers.add(new CharacterValueParser());
        valueParsers.add(new ClassValueParser());
        valueParsers.add(new DoubleValueParser());
        valueParsers.add(new FileValueParser());
        valueParsers.add(new FloatValueParser());
        valueParsers.add(new InetAddressValueParser());
        valueParsers.add(new Inet4AddressValueParser());
        valueParsers.add(new Inet6AddressValueParser());
        valueParsers.add(new InetSocketAddressValueParser());
        valueParsers.add(new IntegerValueParser());
        valueParsers.add(new LongValueParser());
        valueParsers.add(new ObjectAsStringValueParser());
        valueParsers.add(new PatternValueParser());
        valueParsers.add(new ShortValueParser());
        valueParsers.add(new StringValueParser());
        valueParsers.add(new UuidValueParser());
        valueParsers.add(new UriValueParser());
        valueParsers.add(new UrlValueParser());

        return Collections.unmodifiableList(valueParsers);
    }

    private final ConfigExtractor configExtractor;

    /**
     * Creates a new {@link com.statemachinesystems.envy.Envy} instance with the given
     * {@link com.statemachinesystems.envy.ValueParserFactory} and {@link com.statemachinesystems.envy.ConfigSource}.
     *
     * @param valueParserFactory  the {@link com.statemachinesystems.envy.ValueParserFactory} to use
     * @param configSource        the {@link com.statemachinesystems.envy.ConfigSource} to use
     */
    public Envy(ValueParserFactory valueParserFactory, ConfigSource configSource) {
        this.configExtractor = new ConfigExtractor(valueParserFactory, configSource);
    }

    /**
     * Builds a proxy configuration object from the given interface.
     *
     * @param configClass  the configuration interface to be proxied
     * @param <T>          the type of the configuration interface
     * @return             a configuration object that implements the interface
     */
    public <T> T proxy(Class<T> configClass) {
        Map<String, Object> values = configExtractor.extractValuesByMethodName(configClass);
        return ProxyInvocationHandler.proxy(configClass, values);
    }
}
