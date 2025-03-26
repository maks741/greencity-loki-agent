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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;
import org.greencity.constant.LogSource;
import org.greencity.dto.LogsRequestDto;
import org.greencity.dto.LogsResponseDto;
import org.greencity.entity.LokiChunk;
import org.greencity.exceptions.HttpRequestExecutionException;
import org.greencity.exceptions.InvalidProtocolException;
import org.greencity.exceptions.UnableToConnectException;
import org.greencity.exceptions.UnexpectedResponseException;
import org.greencity.util.LokiAgentLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HttpService {

    private static final Logger LOGGER = LokiAgentLogger.getLogger(HttpService.class);
    private final CloseableHttpClient HTTP_CLIENT;

    public HttpService(CloseableHttpClient httpClient) {
        this.HTTP_CLIENT = httpClient;
    }

    public void pushToLoki(LokiChunk lokiChunk, LogSource logSource) {
        String lokiPushUrl = EnvVar.LOKI_PUSH_URL();
        LOGGER.fine(LogMessage.STARTING_TO_PUSH_LOGS.message(logSource.jobName(), lokiPushUrl));
        try {
            HttpPost httpPost = new HttpPost(lokiPushUrl);

            HttpEntity httpEntity = buildLokiPushRequestEntity(lokiChunk);
            httpPost.setEntity(httpEntity);

            LOGGER.finer(LogMessage.SUCCESSFULLY_PREPARED_PUSH_LOGS_REQUEST.message(logSource.jobName(), lokiPushUrl));
            HttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            LOGGER.fine(LogMessage.EXECUTED_PUSH_LOGS_REQUEST.message(logSource.jobName(), lokiPushUrl, statusCode));

            int expectedSuccessStatusCode = EnvVar.EXPECTED_LOKI_RESPONSE_STATUS_CODE();
            if (statusCode != expectedSuccessStatusCode) {
                String responseBody = readBody(httpEntity);
                LOGGER.severe(LogMessage.FAILED_LOGS_PUSH.message(logSource.jobName(), lokiPushUrl, responseBody));
                throw new UnexpectedResponseException(
                        LogMessage.UNEXPECTED_RESPONSE_FROM_LOKI.message(statusCode, responseBody)
                );
            }
            LOGGER.info(LogMessage.SUCCESSFUL_PUSH_TO_LOKI.message(logSource.jobName(), lokiPushUrl));
            HTTP_CLIENT.close();
        } catch (HttpHostConnectException e) {
            LOGGER.severe(LogMessage.UNABLE_TO_CONNECT.message(logSource.jobName(), EnvVar.LOKI_PUSH_URL()));
            throw new UnableToConnectException(e);
        } catch (ClientProtocolException e) {
            LOGGER.severe(LogMessage.INVALID_CLIENT_PROTOCOL.message(logSource.jobName()));
            throw new InvalidProtocolException(e);
        } catch (IOException e) {
            LOGGER.severe(LogMessage.UNABLE_TO_EXECUTE_HTTP_REQUEST.message(logSource.jobName(), e.getMessage()));
            throw new HttpRequestExecutionException(e);
        }
    }

    public LogsResponseDto fetchLogs(LogSource logSource) {
        LOGGER.fine(LogMessage.STARTING_TO_FETCH_LOGS.message(logSource.jobName(), logSource.logsUrl()));

        try {
            HttpPost httpPost = new HttpPost(logSource.logsUrl());
            HttpEntity fetchLogsRequestEntity = buildFetchLogsRequestEntity();

            httpPost.setHeader(EnvVar.SECRET_KEY_HEADER(), EnvVar.SECRET_KEY());
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, EnvVar.AUTH_TOKEN());
            httpPost.setEntity(fetchLogsRequestEntity);

            LOGGER.finer(LogMessage.SUCCESSFULLY_PREPARED_FETCH_LOGS_REQUEST.message(logSource.jobName(), logSource.logsUrl()));

            HttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            String responseBody = readBody(httpEntity);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();

            LOGGER.fine(LogMessage.EXECUTED_FETCH_LOGS_REQUEST.message(logSource.jobName(), logSource.logsUrl(), statusCode));

            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.severe(LogMessage.FAILED_LOGS_FETCH.message(logSource.jobName(), logSource.logsUrl(), responseBody));
                throw new UnexpectedResponseException(LogMessage.UNEXPECTED_RESPONSE_FOR_LOGS_REQUEST.message(logSource.jobName(), statusCode, logSource.logsUrl()));
            }

            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray jsonArray = response.getAsJsonArray(EnvVar.RESPONSE_BODY_FIELD());

            LOGGER.fine(LogMessage.AMOUNT_OF_LOG_LINES.message(logSource.jobName(), jsonArray.size(), logSource.logsUrl()));

            List<String> logLines = new ArrayList<>(jsonArray.asList().stream()
                    .map(JsonElement::getAsString)
                    .toList());

            LOGGER.info(LogMessage.SUCCESSFUL_LOGS_FETCH.message(logSource.jobName(), logSource.logsUrl()));

            return new LogsResponseDto(
                    logLines
            );
        } catch (HttpHostConnectException e) {
            LOGGER.severe(LogMessage.UNABLE_TO_CONNECT.message(logSource.jobName(), logSource.logsUrl()));
            throw new UnableToConnectException(e);
        } catch (IOException e) {
            throw new HttpRequestExecutionException(e);
        }
    }

    private HttpEntity buildLokiPushRequestEntity(LokiChunk lokiChunk) {
        Gson gson = new Gson();
        String lokiChunkJson = gson.toJson(lokiChunk);
        return new StringEntity(lokiChunkJson, ContentType.APPLICATION_JSON);
    }

    private HttpEntity buildFetchLogsRequestEntity() {
        Gson gson = new Gson();
        int logsDaysOffset = EnvVar.LOGS_DAYS_OFFSET();
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
