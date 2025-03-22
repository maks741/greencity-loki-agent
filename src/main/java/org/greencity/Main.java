package org.greencity;

import org.greencity.entity.LokiChunk;
import org.greencity.entity.LokiPayload;
import org.greencity.service.HttpService;
import org.greencity.service.LogsParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        LogsParser logsParser = new LogsParser();

        List<String> logLines = httpService.fetchLogLines();
        List<LokiPayload> lokiPayloads = logsParser.parseToLokiPayloads(logLines);
        LokiChunk lokiChunk = new LokiChunk(lokiPayloads);

        httpService.pushToLoki(lokiChunk);
    }
}