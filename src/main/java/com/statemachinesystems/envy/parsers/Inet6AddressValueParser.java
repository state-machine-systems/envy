package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.Inet6Address;

/**
 * {@link ValueParser} implementation for {@link java.net.Inet6Address} values.
 */
public class Inet6AddressValueParser implements ValueParser<Inet6Address> {

    private final InetAddressValueParser valueParser = new InetAddressValueParser();
    @Override
    public Inet6Address parseValue(String value) {
        return (Inet6Address) valueParser.parseValue(value);
    }

    @Override
    public Class<Inet6Address> getValueClass() {
        return Inet6Address.class;
    }
}
