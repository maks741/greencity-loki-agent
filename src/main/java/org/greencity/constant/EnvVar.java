package org.greencity.constant;

public class EnvVar {

    public static String SERVER_TIME_ZONE() {
        return System.getenv("SERVER_TIME_ZONE");
    }

    public static String LOGGING_LEVEL() {
        return System.getenv("LOGGING_LEVEL");
    }

    public static String LOG_TIMESTAMP_PATTERN() {
        return System.getenv("LOG_TIMESTAMP_PATTERN");
    }

    public static String LOG_REGEX() {
        return System.getenv("LOG_REGEX");
    }

    public static String FETCH_LOGS_FROM() {
        return System.getenv("FETCH_LOGS_FROM");
    }

    public static String SECRET_KEY_HEADER() {
        return System.getenv("SECRET_KEY_HEADER");
    }

    public static String SECRET_KEY() {
        return System.getenv("SECRET_KEY");
    }

    public static String RESPONSE_BODY_FIELD() {
        return System.getenv("RESPONSE_BODY_FIELD");
    }

    public static int LOGS_DAYS_OFFSET() {
        return Integer.parseInt(System.getenv("LOGS_DAYS_OFFSET"));
    }

    public static String AUTH_TOKEN() {
        return "Bearer " + System.getenv("AUTH_TOKEN");
    }

    public static int EXPECTED_LOKI_RESPONSE_STATUS_CODE() {
        return Integer.parseInt(System.getenv("EXPECTED_LOKI_RESPONSE_STATUS_CODE"));
    }

    public static String LOKI_PUSH_URL() {
        return System.getenv("LOKI_PUSH_URL");
    }

    public static String GREENCITY_LOGS_URL() {
        return System.getenv("GREENCITY_LOGS_URL");
    }

    public static String GREENCITY_UBS_LOGS_URL() {
        return System.getenv("GREENCITY_UBS_LOGS_URL");
    }

    public static String GREENCITY_USER_LOGS_URL() {
        return System.getenv("GREENCITY_USER_LOGS_URL");
    }
}
