package org.greencity;

import org.greencity.helper.Dotenv;
import org.greencity.service.LokiService;

public class Main {
    public static void main(String[] args) {
        Dotenv.verifyEnvironmentVariables();

        LokiService lokiService = new LokiService();
        lokiService.fetchLogsAndPushToLoki();
    }
}