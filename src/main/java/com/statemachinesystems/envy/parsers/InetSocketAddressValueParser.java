package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InetSocketAddressValueParser implements ValueParser<InetSocketAddress> {

    private static final Pattern HOST_AND_PORT = Pattern.compile("(.*):(\\d+)$");

    @Override
    public InetSocketAddress parseValue(String value) {
        Matcher matcher = HOST_AND_PORT.matcher(value);
        if (! matcher.matches()) {
            throw new IllegalArgumentException("Can't parse InetSocketAddress from " + value);
        }
        String host = matcher.group(1);
        int port = Integer.parseInt(matcher.group(2));
        return InetSocketAddress.createUnresolved(host, port);
    }

    @Override
    public Class<InetSocketAddress> getValueClass() {
        return InetSocketAddress.class;
    }
}
