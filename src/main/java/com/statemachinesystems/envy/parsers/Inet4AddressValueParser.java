package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.Inet4Address;

/**
 * {@link ValueParser} implementation for {@link java.net.Inet4Address} values.
 */
public class Inet4AddressValueParser implements ValueParser<Inet4Address> {

    private final InetAddressValueParser valueParser = new InetAddressValueParser();
    @Override
    public Inet4Address parseValue(String value) {
        return (Inet4Address) valueParser.parseValue(value);
    }

    @Override
    public Class<Inet4Address> getValueClass() {
        return Inet4Address.class;
    }
}
