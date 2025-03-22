package org.greencity.constant;

import org.greencity.helper.Environment;

public class LokiConstants {

    public static final int EXPECTED_LOKI_RESPONSE_STATUS_CODE = Environment.getenvAsInt("EXPECTED_LOKI_RESPONSE_STATUS_CODE");
    public static final String LOKI_PUSH_URL = Environment.getenv("LOKI_PUSH_URL");
}
