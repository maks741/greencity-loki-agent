package org.greencity.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.greencity.constant.LogsSource;
import org.greencity.constant.LokiConstants;
import org.greencity.entity.LokiChunk;
import org.greencity.helper.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpService {

    public void pushToLoki(LokiChunk lokiChunk) {
        try (var httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(LokiConstants.LOKI_PUSH_URL);

            HttpEntity httpEntity = buildHttpEntity(lokiChunk);
            httpPost.setEntity(httpEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode != LokiConstants.EXPECTED_LOKI_RESPONSE_STATUS_CODE) {
                String message = statusLine.getReasonPhrase();
                throw new RuntimeException(
                        "Unexpected response status code from Loki: " + statusCode + "; Message: " + message
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> fetchLogLines(LogsSource logsSource) {
        List<String> logLines;

        try (var httpClient = HttpClients.createDefault()) {
            String logsUrl = logsSource.logsUrl();
            HttpGet httpGet = new HttpGet(logsUrl);
            httpGet.setHeader(Environment.getenv("SECRET_KEY_HEADER"), Environment.getenv("SECRET_KEY"));

            HttpResponse httpResponse = httpClient.execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();

            String body = new String(httpEntity.getContent().readAllBytes());
            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
            logLines = new ArrayList<>(jsonArray.asList().stream()
                    .map(JsonElement::getAsString)
                    .toList());
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
}
