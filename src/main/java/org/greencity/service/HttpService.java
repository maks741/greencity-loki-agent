package org.greencity.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.greencity.constant.LogsSource;
import org.greencity.dto.LogLinesRequestDto;
import org.greencity.dto.LogsFetchResponseDto;
import org.greencity.entity.LokiChunk;
import org.greencity.constant.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class HttpService {

    private static final Logger log = Logger.getLogger(HttpService.class.getName());

    public void pushToLoki(LokiChunk lokiChunk, LogsSource logsSource) {
        try (var httpClient = HttpClients.createDefault()) {
            String lokiPushUrl = Environment.LOKI_PUSH_URL.value();
            HttpPost httpPost = new HttpPost(lokiPushUrl);

            HttpEntity httpEntity = buildLokiPushRequestEntity(lokiChunk);
            httpPost.setEntity(httpEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            int expectedSuccessStatusCode = Environment.EXPECTED_LOKI_RESPONSE_STATUS_CODE.intValue();
            if (statusCode != expectedSuccessStatusCode) {
                String message = statusLine.getReasonPhrase();
                throw new RuntimeException(
                        "Unexpected response status code from Loki: " + statusCode + "; Message: " + message
                );
            }
            log.info("Successfully pushed logs for job " + logsSource.jobName() + " to " + lokiPushUrl);
        } catch (HttpHostConnectException e) {
            log.warning("Job %s can not connect to %s".formatted(logsSource.jobName(), Environment.LOKI_PUSH_URL.value()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LogsFetchResponseDto fetchLogLines(LogsSource logsSource) {
        return fetchLogs(logsSource).map(httpResponse -> {
            String responseBody = readBody(httpResponse);

            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray jsonArray = response.getAsJsonArray(Environment.RESPONSE_BODY_FIELD.value());

            List<String> logLines = new ArrayList<>(jsonArray.asList().stream()
                    .map(JsonElement::getAsString)
                    .toList());

            log.info("Successfully fetched logs for job " + logsSource.jobName() + " from url: " + logsSource.logsUrl());

            return new LogsFetchResponseDto(
                    logLines,
                    true
            );
        }).orElse(LogsFetchResponseDto.unfetched());
    }

    private Optional<HttpResponse> fetchLogs(LogsSource logsSource) {
        HttpResponse httpResponse = null;

        try (var httpClient = HttpClients.createDefault()) {
            String logsUrl = logsSource.logsUrl();
            HttpPost httpPost = new HttpPost(logsUrl);
            HttpEntity fetchLogsRequestEntity = buildFetchLogsRequestEntity();

            httpPost.setHeader(Environment.SECRET_KEY_HEADER.value(), Environment.SECRET_KEY.value());
            httpPost.setEntity(fetchLogsRequestEntity);

            httpResponse = httpClient.execute(httpPost);

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            String responseBody = readBody(httpResponse);

            if (statusCode != HttpStatus.SC_OK) {
                log.warning(
                        """
                                Job %s can not fetch logs from  %s
                                Response: %s
                                """.formatted(logsSource.jobName(), logsSource.logsUrl(), responseBody)
                );
                return Optional.empty();
            }
        } catch (HttpHostConnectException e) {
            log.warning("Job %s can not connect to %s".formatted(logsSource.jobName(), logsSource.logsUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(httpResponse);
    }

    private HttpEntity buildLokiPushRequestEntity(LokiChunk lokiChunk) {
        Gson gson = new Gson();
        String lokiChunkJson = gson.toJson(lokiChunk);
        return new StringEntity(lokiChunkJson, ContentType.APPLICATION_JSON);
    }

    private HttpEntity buildFetchLogsRequestEntity() {
        Gson gson = new Gson();
        Integer logsDaysOffset = Environment.LOGS_DAYS_OFFSET.intValue();
        LogLinesRequestDto logLinesRequestDto = new LogLinesRequestDto(logsDaysOffset);

        String logLinesRequestJson = gson.toJson(logLinesRequestDto);
        return new StringEntity(logLinesRequestJson, ContentType.APPLICATION_JSON);
    }

    private String readBody(HttpResponse httpResponse) {
        HttpEntity httpEntity = httpResponse.getEntity();

        String responseBody;
        try (InputStream content = httpEntity.getContent()) {
            responseBody = new String(content.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }
}
