package org.greencity.helper;

public class Environment {

    public static String getenv(String environmentVariableName) {
        return System.getenv(environmentVariableName);
    }

    public static int getenvAsInt(String environmentVariableName) {
        return Integer.parseInt(getenv(environmentVariableName));
    }

}
