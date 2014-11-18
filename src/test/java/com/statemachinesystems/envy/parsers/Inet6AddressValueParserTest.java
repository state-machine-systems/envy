package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Inet6AddressValueParserTest {

    @Test
    public void parsesInet6Addresses() throws UnknownHostException {
        String address = "fe80::1";
        assertThat(new Inet6AddressValueParser().parseValue(address), is(InetAddress.getByName(address)));
    }

    @Test(expected = ClassCastException.class)
    public void throwsClassCastExceptionForInet4Address() {
        new Inet6AddressValueParser().parseValue("127.0.0.1");
    }
}
