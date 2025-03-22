package org.greencity.helper;

import java.util.function.Function;

public class RemoveAnsiEscapeCodesFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

}
