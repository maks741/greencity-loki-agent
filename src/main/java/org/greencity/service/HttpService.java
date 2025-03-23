package org.greencity.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;
import org.greencity.constant.LogSource;
import org.greencity.dto.LogsRequestDto;
import org.greencity.dto.LogsResponseDto;
import org.greencity.entity.LokiChunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HttpService {

    private static final Logger log = Logger.getLogger(HttpService.class.getName());

    public void pushToLoki(LokiChunk lokiChunk, LogSource logSource) {
        try (var httpClient = HttpClients.createDefault()) {
            String lokiPushUrl = EnvVar.LOKI_PUSH_URL.value();
            HttpPost httpPost = new HttpPost(lokiPushUrl);

            HttpEntity httpEntity = buildLokiPushRequestEntity(lokiChunk);
            httpPost.setEntity(httpEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            int expectedSuccessStatusCode = EnvVar.EXPECTED_LOKI_RESPONSE_STATUS_CODE.intValue();
            if (statusCode != expectedSuccessStatusCode) {
                String message = statusLine.getReasonPhrase();
                throw new RuntimeException(
                        LogMessage.UNEXPECTED_RESPONSE_FROM_LOKI.message(statusCode, message)
                );
            }
            log.info(LogMessage.SUCCESSFUL_PUSH_TO_LOKI.message(logSource.jobName(), lokiPushUrl));
        } catch (HttpHostConnectException e) {
            log.warning(LogMessage.UNABLE_TO_CONNECT.message(logSource.jobName(), EnvVar.LOKI_PUSH_URL.value()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LogsResponseDto fetchLogs(LogSource logSource) {
        try (var httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(logSource.logsUrl());
            HttpEntity fetchLogsRequestEntity = buildFetchLogsRequestEntity();

            httpPost.setHeader(EnvVar.SECRET_KEY_HEADER.value(), EnvVar.SECRET_KEY.value());
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, EnvVar.AUTH_TOKEN.value());
            httpPost.setEntity(fetchLogsRequestEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();

            String responseBody = readBody(httpEntity);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                log.warning(LogMessage.FAILED_LOGS_FETCH.message(logSource.jobName(), logSource.logsUrl(), responseBody));
                return LogsResponseDto.unfetched();
            }

            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray jsonArray = response.getAsJsonArray(EnvVar.RESPONSE_BODY_FIELD.value());

            List<String> logLines = new ArrayList<>(jsonArray.asList().stream()
                    .map(JsonElement::getAsString)
                    .toList());

            log.info(LogMessage.SUCCESSFUL_LOGS_FETCH.message(logSource.jobName(), logSource.logsUrl()));

            return new LogsResponseDto(
                    logLines,
                    true
            );
        } catch (HttpHostConnectException e) {
            log.warning(LogMessage.UNABLE_TO_CONNECT.message(logSource.jobName(), logSource.logsUrl()));
            return LogsResponseDto.unfetched();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity buildLokiPushRequestEntity(LokiChunk lokiChunk) {
        Gson gson = new Gson();
        String lokiChunkJson = gson.toJson(lokiChunk);
        return new StringEntity(lokiChunkJson, ContentType.APPLICATION_JSON);
    }

    private HttpEntity buildFetchLogsRequestEntity() {
        Gson gson = new Gson();
        Integer logsDaysOffset = EnvVar.LOGS_DAYS_OFFSET.intValue();
        LogsRequestDto logsRequestDto = new LogsRequestDto(logsDaysOffset);

        String logLinesRequestJson = gson.toJson(logsRequestDto);
        return new StringEntity(logLinesRequestJson, ContentType.APPLICATION_JSON);
    }

    private String readBody(HttpEntity httpEntity) {
        try {
            return EntityUtils.toString(httpEntity, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
