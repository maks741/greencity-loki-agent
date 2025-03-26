package org.greencity.service;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.greencity.constant.EnvVar;
import org.greencity.constant.LogSource;
import org.greencity.entity.LokiChunk;
import org.greencity.exceptions.HttpRequestExecutionException;
import org.greencity.exceptions.InvalidProtocolException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class HttpServiceTest {

    private HttpService httpService;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockHttpResponse;
    private StatusLine mockStatusLine;
    private MockedStatic<EnvVar> mockEnvVar;
    private MockedStatic<LokiAgentLogger> mockLokiAgentLogger;
    private static final Logger loggerMock = mock(Logger.class);
    private static LogSource mockLogSource;
    private static final String VALID_URI = "http://192.168.0.1/";
    private static final String KEY_HEADER = "KEY_HEADER";
    private static final String SECRET_KEY = "SECRET KEY";
    private static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String LOGGING_LEVEL = "INFO";
    private static final int EXPECTED_STATUS_CODE = 200;

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
    void pushToLokiSuccessTest() throws Exception {
        LokiChunk lokiChunk = new LokiChunk(List.of());

        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn(LOGGING_LEVEL);
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(EXPECTED_STATUS_CODE);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn(VALID_URI);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(EXPECTED_STATUS_CODE);

        httpService = new HttpService(mockHttpClient);

        assertDoesNotThrow(() -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).info(anyString());
    }

    @Test
    void pushToLokiBadStatusCodeFailureTest() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());

        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn(LOGGING_LEVEL);
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(EXPECTED_STATUS_CODE);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn(VALID_URI);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(400);

        httpService = new HttpService(mockHttpClient);

        assertThrows(UnexpectedResponseException.class, () -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void pushToLokiBadHostFailureTest() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());

        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn(LOGGING_LEVEL);
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(EXPECTED_STATUS_CODE);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn(VALID_URI);
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new ClientProtocolException());

        httpService = new HttpService(mockHttpClient);

        assertThrows(InvalidProtocolException.class, () -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void pushToLokiBadHTTPRequestFailureTest() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());

        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn(LOGGING_LEVEL);
        mockEnvVar.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(EXPECTED_STATUS_CODE);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn(VALID_URI);
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new IOException());

        httpService = new HttpService(mockHttpClient);

        assertThrows(HttpRequestExecutionException.class, () -> httpService.pushToLoki(lokiChunk, mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void pushToLokiHostExceptionFailureTest() throws IOException {
        LokiChunk lokiChunk = new LokiChunk(List.of());
        LogSource logSource = LogSource.GREENCITY;

        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        mockEnvVar.when(EnvVar::LOGGING_LEVEL).thenReturn(LOGGING_LEVEL);
        mockEnvVar.when(EnvVar::LOKI_PUSH_URL).thenReturn("http://localhost/");
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new HttpHostConnectException(null, null, null));

        httpService = new HttpService(mockHttpClient);

        assertThrows(UnableToConnectException.class, () -> httpService.pushToLoki(lokiChunk, logSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void fetchLogsSuccessTest() throws Exception {
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        when(mockLogSource.logsUrl()).thenReturn(VALID_URI);
        httpService = new HttpService(mockHttpClient);
        mockEnvVar.when(EnvVar::SECRET_KEY_HEADER).thenReturn(KEY_HEADER);
        mockEnvVar.when(EnvVar::SECRET_KEY).thenReturn(SECRET_KEY);
        mockEnvVar.when(EnvVar::AUTH_TOKEN).thenReturn(AUTH_TOKEN);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);

        HttpEntity httpEntity = new StringEntity("{\"log\":[]}");

        when(mockHttpResponse.getEntity()).thenReturn(httpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(EXPECTED_STATUS_CODE);
        mockEnvVar.when(EnvVar::RESPONSE_BODY_FIELD).thenReturn("log");

        assertDoesNotThrow(() -> httpService.fetchLogs(mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).info(anyString());
    }

    @Test
    void fetchLogsInvalidStatusCodeFailureTest() throws Exception {
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        when(mockLogSource.logsUrl()).thenReturn(VALID_URI);
        httpService = new HttpService(mockHttpClient);
        mockEnvVar.when(EnvVar::SECRET_KEY_HEADER).thenReturn(KEY_HEADER);
        mockEnvVar.when(EnvVar::SECRET_KEY).thenReturn(SECRET_KEY);
        mockEnvVar.when(EnvVar::AUTH_TOKEN).thenReturn(AUTH_TOKEN);
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new HttpHostConnectException(null, null, null));

        assertThrows(UnableToConnectException.class, () -> httpService.fetchLogs(mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }

    @Test
    void fetchLogsBadStatusCode() throws Exception {
        mockLokiAgentLogger.when(() -> LokiAgentLogger.getLogger(HttpService.class)).thenReturn(loggerMock);
        when(mockLogSource.logsUrl()).thenReturn(VALID_URI);
        mockEnvVar.when(EnvVar::SECRET_KEY_HEADER).thenReturn(KEY_HEADER);
        mockEnvVar.when(EnvVar::SECRET_KEY).thenReturn(SECRET_KEY);
        mockEnvVar.when(EnvVar::AUTH_TOKEN).thenReturn(AUTH_TOKEN);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);

        HttpEntity httpEntity = new StringEntity("{\"log\":[]}");

        when(mockHttpResponse.getEntity()).thenReturn(httpEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(418);

        httpService = new HttpService(mockHttpClient);

        assertThrows(UnexpectedResponseException.class, () -> httpService.fetchLogs(mockLogSource));
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class));
        verify(loggerMock, times(1)).severe(anyString());
    }
}
