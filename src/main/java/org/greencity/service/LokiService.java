package org.greencity.service;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogsSource;
import org.greencity.dto.LogsResponseDto;
import org.greencity.entity.LokiChunk;
import org.greencity.entity.LokiPayload;

import java.util.Arrays;
import java.util.List;

public class LokiService {

    public void fetchLogsAndPushToLoki() {
        List<LogsSource> logsSources = determineLogsSources();

        for (LogsSource logsSource : logsSources) {
            fetchLogsAndPushToLoki(logsSource);
        }
    }

    private void fetchLogsAndPushToLoki(LogsSource logsSource) {
        HttpService httpService = new HttpService();
        LogsParser logsParser = new LogsParser();

        LogsResponseDto logsResponseDto = httpService.fetchLogs(logsSource);

        if (!logsResponseDto.fetched()) {
            return;
        }

        List<String> logLines = logsResponseDto.logs();
        List<LokiPayload> lokiPayloads = logsParser.parseToLokiPayloads(logsSource, logLines);
        LokiChunk lokiChunk = new LokiChunk(lokiPayloads);

        httpService.pushToLoki(lokiChunk, logsSource);
    }

    private List<LogsSource> determineLogsSources() {
        return Arrays.stream(EnvVar.FETCH_LOGS_FROM.value()
                .split(","))
                .map(LogsSource::valueOf)
                .toList();
    }

}
