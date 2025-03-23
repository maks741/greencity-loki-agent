package org.greencity;

import org.greencity.constant.EnvVar;
import org.greencity.service.LokiService;

public class Main {
    public static void main(String[] args) {
        EnvVar.verifyEnvironmentVariables();

        LokiService lokiService = new LokiService();
        lokiService.fetchLogsAndPushToLoki();
    }
}