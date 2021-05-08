package utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestUtility {
    public static InputStream giveInput(String input) {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return stdin;
    }
}
