package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InetSocketAddressValueParserTest {

    @Test
    public void parsesIpv4InetSocketAddress() {
        String host = "1.1.1.1";
        int port = 123;
        assertThat(new InetSocketAddressValueParser().parseValue(host + ":" + port),
                is(InetSocketAddress.createUnresolved(host, port)));
    }

    @Test
    public void parsesIpv6InetSocketAddress() {
        String host = "[::0]";
        int port = 123;
        assertThat(new InetSocketAddressValueParser().parseValue(host + ":" + port),
                is(InetSocketAddress.createUnresolved(host, port)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsImproperlyFormattedHostAndPort() {
        new InetSocketAddressValueParser().parseValue("foo");
    }
}
