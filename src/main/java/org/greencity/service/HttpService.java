package org.greencity.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.maks.test_grafana_plugin.entity.LokiChunk;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpService {

    public void pushToLoki(LokiChunk lokiChunk) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LokiChunk> requestEntity = new HttpEntity<>(lokiChunk, headers);

        String lokiUrl = "http://192.168.0.170:3100/loki/api/v1/push";
        HttpStatusCode statusCode = restTemplate.exchange(lokiUrl, HttpMethod.POST, requestEntity, String.class).getStatusCode();
        System.out.println("statusCode: " + statusCode);
    }

    public List<String> fetchLogLines() {
        List<String> logLines;

        try (var httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/logs");

            HttpResponse httpResponse = httpClient.execute(httpGet);

            org.apache.http.HttpEntity httpEntity = httpResponse.getEntity();

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
}
