package com.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RunWireMock {
    private static final Logger logger = LoggerFactory.getLogger(RunWireMock.class);

    public static void main(String[] args) {
        WireMockServer wireMockServer = new WireMockServer(8080);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping WireMock server...");
            wireMockServer.stop();
        }));

        try {
            wireMockServer.start();
            configureFor("localhost", 8080);
            logger.info("WireMock server started on port 8080");

            stubFor(get(urlEqualTo("/test/urlequal"))
                    .willReturn(aResponse().withBody("Welcome to test!")));
            logger.info("Stub for /test/urlequal created");

            // Ожидание завершения процесса
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.error("WireMock server interrupted", e);
        } finally {
            wireMockServer.stop();
            logger.info("WireMock server stopped");
        }
    }
}
