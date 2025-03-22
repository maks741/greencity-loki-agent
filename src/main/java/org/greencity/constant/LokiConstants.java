package org.greencity.constant;

public class LokiConstants {

    public static final int EXPECTED_SUCCESS_RESPONSE_STATUS_CODE = 204;

    // TODO: When that app will be inside the docker container the url will be http://loki:3100/loki/api/v1/push
    // where 'loki' is the name of the Loki container
    public static final String LOKI_PUSH_URL = "http://192.168.0.170:3100/loki/api/v1/push";

}
