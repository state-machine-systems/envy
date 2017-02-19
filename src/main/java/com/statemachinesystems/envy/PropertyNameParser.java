package com.statemachinesystems.envy;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

/**
 * Splits a bean property name into component words.
 */
public class PropertyNameParser {

    private enum ParserState {
        INITIAL, ACCUMULATING, ACCUMULATING_WITH_INITIAL_CAPS
    }

    private static boolean isEof(int ch) {
        return ch < 0;
    }

    private static IllegalArgumentException unexpectedInput(int ch) {
        return new IllegalArgumentException("Unexpected input character: " + (char) ch);
    }

    private final String propertyName;
    private final StringBuilder buf;
    private final List<String> parts;

    public PropertyNameParser(String propertyName) {
        this.propertyName = propertyName;
        this.buf = new StringBuilder();
        this.parts = new ArrayList<String>();
    }

    public List<String> parse() {
        StringReader reader = new StringReader(propertyName);
        try {
            return parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<String> parse(StringReader reader) throws IOException {
        ParserState state = ParserState.INITIAL;
        init();

        while (true) {
            reader.mark(2);
            int ch = reader.read();

            switch (state) {
                case INITIAL:
                    if (isLowerCase(ch)) {
                        buffer(ch);
                        state = ParserState.ACCUMULATING;
                    } else if (isUpperCase(ch)) {
                        buffer(ch);
                        state = ParserState.ACCUMULATING_WITH_INITIAL_CAPS;
                    } else if (isEof(ch)) {
                        return parts;
                    } else {
                        throw unexpectedInput(ch);
                    }
                    break;
                case ACCUMULATING:
                    if (isLowerCase(ch)) {
                        buffer(ch);
                    } else if (isUpperCase(ch)) {
                        reader.reset();
                        emit();
                        state = ParserState.INITIAL;
                    } else if (isDigit(ch)) {
                        buffer(ch);
                    } else if (isEof(ch)) {
                        emit();
                        return parts;
                    } else {
                        throw unexpectedInput(ch);
                    }
                    break;
                case ACCUMULATING_WITH_INITIAL_CAPS:
                    if (isLowerCase(ch)) {
                        buffer(ch);
                        state = ParserState.ACCUMULATING;
                    } else if (isUpperCase(ch)) {
                        int lookahead = reader.read();
                        if (isLowerCase(lookahead)) {
                            reader.reset();
                            emit();
                            state = ParserState.INITIAL;
                        } else {
                            reader.reset();
                            ch = reader.read();
                            buffer(ch);
                        }
                    } else if (isDigit(ch)) {
                        buffer(ch);
                        state = ParserState.ACCUMULATING;
                    } else if (isEof(ch)) {
                        emit();
                        return parts;
                    } else {
                        throw unexpectedInput(ch);
                    }
                    break;
            }
        }
    }

    private void init() {
        buf.setLength(0);
        parts.clear();
    }

    private void buffer(int ch) {
        buf.append((char) ch);
    }

    private void emit() {
        parts.add(buf.toString());
        buf.setLength(0);
    }
}
