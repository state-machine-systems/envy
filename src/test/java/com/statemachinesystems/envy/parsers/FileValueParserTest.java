package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileValueParserTest {

    @Test
    public void parsesFiles() {
        String fileName = "non/existent/file.txt";
        assertThat(new FileValueParser().parseValue(fileName), is(new File(fileName)));
    }
}
