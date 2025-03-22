package org.greencity;

import org.greencity.constant.LogsSource;
import org.greencity.service.LokiService;

public class Main {
    public static void main(String[] args) {
        LokiService lokiService = new LokiService();

        for (LogsSource logsSource : LogsSource.values()) {
            lokiService.fetchLogsAndPushToLoki(logsSource);
        }
    }
}