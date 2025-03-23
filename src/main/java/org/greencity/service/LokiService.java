package org.greencity.service;

import org.greencity.constant.LogsSource;
import org.greencity.dto.LogsFetchResponseDto;
import org.greencity.entity.LokiChunk;
import org.greencity.entity.LokiPayload;

import java.util.List;

public class LokiService {

    public void fetchLogsAndPushToLoki(LogsSource logsSource) {
        HttpService httpService = new HttpService();
        LogsParser logsParser = new LogsParser();

        LogsFetchResponseDto logsFetchResponseDto = httpService.fetchLogLines(logsSource);

        if (!logsFetchResponseDto.fetched()) {
            return;
        }

        List<String> logLines = logsFetchResponseDto.logs();
        List<LokiPayload> lokiPayloads = logsParser.parseToLokiPayloads(logsSource, logLines);
        LokiChunk lokiChunk = new LokiChunk(lokiPayloads);

        httpService.pushToLoki(lokiChunk, logsSource);
    }

}
