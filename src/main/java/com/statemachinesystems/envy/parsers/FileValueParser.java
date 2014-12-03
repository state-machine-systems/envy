package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.io.File;

/**
 * {@link ValueParser} implementation for {@link java.io.File} values.
 */
public class FileValueParser implements ValueParser<File> {

    @Override
    public File parseValue(String value) {
        return new File(value);
    }

    @Override
    public Class<File> getValueClass() {
        return File.class;
    }
}
