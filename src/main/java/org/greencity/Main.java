package org.greencity;

import org.greencity.service.LokiService;
import org.greencity.util.EnvUtils;

public class Main {
    public static void main(String[] args) {
        EnvUtils.verifyEnvironmentVariables();

        LokiService lokiService = new LokiService();
        lokiService.fetchLogsAndPushToLoki();
    }
}