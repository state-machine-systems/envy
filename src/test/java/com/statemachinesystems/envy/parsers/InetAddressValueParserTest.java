package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InetAddressValueParserTest {

    @Test
    public void parsesInetAddresses() throws UnknownHostException {
        String name = "127.0.0.1";
        assertThat(new InetAddressValueParser().parseValue(name), is(InetAddress.getByName(name)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rethrowsUnknownHostExceptionAsIllegalArgumentException() {
        new InetAddressValueParser().parseValue("999.999.999.999");
    }
}
