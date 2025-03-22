package org.greencity.service;

import org.greencity.constant.LogsSource;
import org.greencity.entity.LokiChunk;
import org.greencity.entity.LokiPayload;

import java.util.List;

public class LokiService {

    public void fetchLogsAndPushToLoki(LogsSource logsSource) {
        HttpService httpService = new HttpService();
        LogsParser logsParser = new LogsParser();

        List<String> logLines = httpService.fetchLogLines(logsSource);
        List<LokiPayload> lokiPayloads = logsParser.parseToLokiPayloads(logsSource, logLines);
        LokiChunk lokiChunk = new LokiChunk(lokiPayloads);

        httpService.pushToLoki(lokiChunk);
    }

}
