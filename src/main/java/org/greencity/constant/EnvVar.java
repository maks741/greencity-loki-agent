package org.greencity.constant;

public enum EnvVar {

    SERVER_TIME_ZONE(System.getenv("SERVER_TIME_ZONE")),
    LOGGING_LEVEL(System.getenv("LOGGING_LEVEL")),
    LOG_TIMESTAMP_PATTERN(System.getenv("LOG_TIMESTAMP_PATTERN")),

    FETCH_LOGS_FROM(System.getenv("FETCH_LOGS_FROM")),
    SECRET_KEY_HEADER(System.getenv("SECRET_KEY_HEADER")),
    SECRET_KEY(System.getenv("SECRET_KEY")),
    RESPONSE_BODY_FIELD(System.getenv("RESPONSE_BODY_FIELD")),
    LOGS_DAYS_OFFSET(System.getenv("LOGS_DAYS_OFFSET")),
    AUTH_TOKEN("Bearer " + System.getenv("AUTH_TOKEN")),

    EXPECTED_LOKI_RESPONSE_STATUS_CODE(System.getenv("EXPECTED_LOKI_RESPONSE_STATUS_CODE")),
    LOKI_PUSH_URL(System.getenv("LOKI_PUSH_URL")),

    GREENCITY_LOGS_URL(System.getenv("GREENCITY_LOGS_URL")),
    GREENCITY_UBS_LOGS_URL(System.getenv("GREENCITY_UBS_LOGS_URL")),
    GREENCITY_USER_LOGS_URL(System.getenv("GREENCITY_USER_LOGS_URL"));

    private final String value;

    EnvVar(String value) {
        this.value = value;
    }

    public static void verifyEnvironmentVariables() {
        for (EnvVar envVar : values()) {
            if (envVar.value() == null) {
                throw new RuntimeException(
                        LogMessage.ENV_VAR_NOT_FOUND.message(envVar.name())
                );
            }
        }
    }

    public String value() {
        return value;
    }

    public Integer intValue() {
        return Integer.parseInt(value);
    }
}
