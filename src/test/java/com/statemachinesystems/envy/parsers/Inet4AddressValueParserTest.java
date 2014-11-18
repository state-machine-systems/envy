package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Inet4AddressValueParserTest {

    @Test
    public void parsesInet4Addresses() throws UnknownHostException {
        String name = "127.0.0.1";
        assertThat(new Inet4AddressValueParser().parseValue(name), is(InetAddress.getByName(name)));
    }

    @Test(expected = ClassCastException.class)
    public void throwsClassCastExceptionForInet6Address() {
        new Inet4AddressValueParser().parseValue("fe80::1");
    }
}
