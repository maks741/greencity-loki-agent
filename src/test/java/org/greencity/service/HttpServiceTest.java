package org.greencity.service;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.greencity.constant.EnvVar;
import org.greencity.constant.LogSource;
import org.greencity.entity.LokiChunk;
import org.greencity.exceptions.UnableToConnectException;
import org.greencity.exceptions.UnexpectedResponseException;
import org.greencity.util.LokiAgentLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpServiceTest {

    private HttpService httpService;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockHttpResponse;
    private StatusLine mockStatusLine;
    private MockedStatic<EnvVar> mockEnvVar;
    private MockedStatic<LokiAgentLogger> mockLokiAgentLogger;
    private static final Logger loggerMock = mock(Logger.class);
    private static LogSource mockLogSource;
    @BeforeEach
    void setUp() {
        mockHttpClient = mock(CloseableHttpClient.class);
        mockHttpResponse = mock(CloseableHttpResponse.class);
        mockStatusLine = mock(StatusLine.class);
        mockEnvVar = mockStatic(EnvVar.class);
        mockLokiAgentLogger = mockStatic(LokiAgentLogger.class);
        mockLogSource = mock(LogSource.class);
    }
    @AfterEach
    void tearDown() {
        clearInvocations(loggerMock);
        mockEnvVar.close();
        mockLokiAgentLogger.close();
    }
    @Test
    void testPushToLokiSuccess() throws Exception {
        LokiChunk lokiChunk = new LokiChunk(List.of());
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn("INFO");
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(200);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn("http://192.168.0.1/");
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        httpService = new HttpService(mockHttpClient);
        assertDoesNotThrow(() -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).info(anyString());
    }

    @Test
    void testPushToLokiBadStatusCodeFailure() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn("INFO");
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(200);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn("http://192.168.0.1/");
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(400);
        httpService = new HttpService(mockHttpClient);
        assertThrows(UnexpectedResponseException.class, () -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void testPushToLokiHostExceptionFailure() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());
        LogSource logSource = LogSource.GREENCITY;
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn("INFO");
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn("http://localhost/");
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new HttpHostConnectException(null, null));
        httpService = new HttpService(mockHttpClient);
        assertThrows(UnableToConnectException.class, () -> httpService.pushToLoki(lokiChunk, logSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void testFetchLogs_Success() throws Exception {
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        when(mockLogSource.logsUrl()).thenReturn("http://192.168.0.1/");
        httpService = new HttpService(mockHttpClient);
        mockEnvVar.when(EnvVar::SECRET_KEY_HEADER).thenReturn("KEY HEADER");
        mockEnvVar.when(EnvVar::SECRET_KEY).thenReturn("SECRET KEY");
        mockEnvVar.when(EnvVar::AUTH_TOKEN).thenReturn("AUTH TOKEN");
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        HttpEntity httpEntity = new StringEntity("{\"log\":[]}");
        when(mockHttpResponse.getEntity()).thenReturn(httpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        mockEnvVar.when(EnvVar::RESPONSE_BODY_FIELD).thenReturn("log");
        httpService.fetchLogs(mockLogSource);
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).info(anyString());
    }

    @Test
    void testFetchLogsInvalidStatusCodeFailure() throws Exception {
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        when(mockLogSource.logsUrl()).thenReturn("http://192.168.0.1/");
        httpService = new HttpService(mockHttpClient);
        mockEnvVar.when(EnvVar::SECRET_KEY_HEADER).thenReturn("KEY HEADER");
        mockEnvVar.when(EnvVar::SECRET_KEY).thenReturn("SECRET KEY");
        mockEnvVar.when(EnvVar::AUTH_TOKEN).thenReturn("AUTH TOKEN");
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new HttpHostConnectException(null, null));
        assertThrows(UnableToConnectException.class, () -> httpService.fetchLogs(mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }
}
