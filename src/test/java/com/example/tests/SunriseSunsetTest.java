package com.example.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class SunriseSunsetTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetTest.class);

    @Test
    public void testSunriseSunset() {
        logger.info("Запуск testSunriseSunset");

        // Мокируем успешный ответ
        String mockResponse = "{ \"Sunrise\": \"2025-02-22T06:30:00\", \"Sunset\": \"2025-02-22T18:00:00\" }";

        // Статика для WireMock
        stubFor(get(urlPathEqualTo("/astronomy/v1/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для расчета времени восхода и заката");

        // Выполняем запрос через RestAssured
        String response = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/astronomy/v1/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .asString();

        // Проверяем, что ответ не пустой
        assertNotNull(response);
        assertTrue(response.contains("Sunrise"));
        assertTrue(response.contains("Sunset"));
        logger.info("Тест testSunriseSunset завершен успешно");
    }

    @Test
    public void testSunriseSunsetNotFound() {
        logger.info("Запуск testSunriseSunsetNotFound");

        // Мокируем ошибку с неверным ID
        stubFor(get(urlPathEqualTo("/astronomy/v1/000000"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Astronomy data not found\"}")));

        logger.debug("Создан мок для запроса с неверным ID");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/astronomy/v1/000000")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
        logger.info("Тест testSunriseSunsetNotFound завершен успешно");
    }
}
