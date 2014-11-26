package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressValueParser implements ValueParser<InetAddress> {

    @Override
    public InetAddress parseValue(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Class<InetAddress> getValueClass() {
        return InetAddress.class;
    }
}
