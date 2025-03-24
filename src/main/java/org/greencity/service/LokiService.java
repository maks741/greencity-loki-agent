package org.greencity.service;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogSource;
import org.greencity.dto.LogsResponseDto;
import org.greencity.entity.LokiChunk;
import org.greencity.entity.LokiPayload;

import java.util.Arrays;
import java.util.List;

public class LokiService {

    public void fetchLogsAndPushToLoki() {
        List<LogSource> logSources = determineLogsSources();

        for (LogSource logSource : logSources) {
            fetchLogsAndPushToLoki(logSource);
        }
    }

    private void fetchLogsAndPushToLoki(LogSource logSource) {
        HttpService httpService = new HttpService();
        LogsParser logsParser = new LogsParser();

        LogsResponseDto logsResponseDto = httpService.fetchLogs(logSource);
        List<String> logLines = logsResponseDto.logs();

        List<LokiPayload> lokiPayloads = logsParser.parseToLokiPayloads(logSource, logLines);
        LokiChunk lokiChunk = new LokiChunk(lokiPayloads);

        httpService.pushToLoki(lokiChunk, logSource);
    }

    private List<LogSource> determineLogsSources() {
        return Arrays.stream(EnvVar.FETCH_LOGS_FROM.value()
                .split(","))
                .map(LogSource::valueOf)
                .toList();
    }

}
