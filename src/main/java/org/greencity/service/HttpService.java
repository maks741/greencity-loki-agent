package org.greencity.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.greencity.constant.LogsSource;
import org.greencity.entity.LokiChunk;
import org.greencity.helper.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class HttpService {

    private static final Logger log = Logger.getLogger(HttpService.class.getName());

    public HttpService() {
        initLogger();
    }

    public void pushToLoki(LokiChunk lokiChunk, LogsSource logsSource) {
        try (var httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(Environment.LOKI_PUSH_URL.value());

            HttpEntity httpEntity = buildHttpEntity(lokiChunk);
            httpPost.setEntity(httpEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            int expectedSuccessStatusCode = Integer.parseInt(Environment.EXPECTED_LOKI_RESPONSE_STATUS_CODE.value());
            if (statusCode != expectedSuccessStatusCode) {
                String message = statusLine.getReasonPhrase();
                throw new RuntimeException(
                        "Unexpected response status code from Loki: " + statusCode + "; Message: " + message
                );
            }
            log.info("Successfully pushed logs for job " + logsSource.jobName() + " to Loki");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> fetchLogLines(LogsSource logsSource) {
        List<String> logLines;

        try (var httpClient = HttpClients.createDefault()) {
            String logsUrl = logsSource.logsUrl();
            HttpGet httpGet = new HttpGet(logsUrl);
            httpGet.setHeader(Environment.SECRET_KEY_HEADER.value(), Environment.SECRET_KEY.value());

            HttpResponse httpResponse = httpClient.execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                String message = statusLine.getReasonPhrase();
                throw new RuntimeException(
                        """
                        Job %s can not fetch logs from  %s
                        Response status code: %s, message: %s
                        """.formatted(logsSource.jobName(), logsUrl, statusCode, message)
                );
            }

            String body = new String(httpEntity.getContent().readAllBytes());
            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
            logLines = new ArrayList<>(jsonArray.asList().stream()
                    .map(JsonElement::getAsString)
                    .toList());

            log.info("Successfully fetched logs for job " + logsSource.jobName() + " from url: " + logsSource.logsUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return logLines;
    }

    private HttpEntity buildHttpEntity(LokiChunk lokiChunk) {
        Gson gson = new Gson();
        String lokiChunkJson = gson.toJson(lokiChunk);
        return new StringEntity(lokiChunkJson, ContentType.APPLICATION_JSON);
    }

    private void initLogger() {
        log.setUseParentHandlers(false);
        log.addHandler(new ConsoleHandler() {
            {
                setOutputStream(System.out);
            }
        });
    }
}
