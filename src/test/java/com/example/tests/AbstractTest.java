package com.example.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import java.net.http.HttpResponse;

abstract class AbstractTest {
    private static final WireMockServer wireMockServer = new WireMockServer(8080);
    private static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);
    protected static final String API_KEY = "your_api_key_here";
    private static String baseUrl;

    @BeforeAll
    static void startServer() {
        baseUrl = "http://localhost:8080";
        wireMockServer.start();
        configureFor("localhost", 8080);
        logger.info("WireMock сервер запущен на порту 8080");
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
        logger.info("WireMock сервер остановлен");
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public String convertResponseToString(HttpResponse<String> response) {
        logger.debug("convertResponseToString вызван");
        return response.body();
    }
}
