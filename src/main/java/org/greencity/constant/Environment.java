package org.greencity.constant;

public enum Environment {

    SECRET_KEY_HEADER(System.getenv("SECRET_KEY_HEADER")),
    SECRET_KEY(System.getenv("SECRET_KEY")),
    RESPONSE_BODY_FIELD(System.getenv("RESPONSE_BODY_FIELD")),

    EXPECTED_LOKI_RESPONSE_STATUS_CODE(System.getenv("EXPECTED_LOKI_RESPONSE_STATUS_CODE")),
    LOKI_PUSH_URL(System.getenv("LOKI_PUSH_URL")),

    GREENCITY_LOGS_URL(System.getenv("GREENCITY_LOGS_URL")),
    GREENCITY_UBS_LOGS_URL(System.getenv("GREENCITY_UBS_LOGS_URL")),
    GREENCITY_USER_LOGS_URL(System.getenv("GREENCITY_USER_LOGS_URL"));

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
