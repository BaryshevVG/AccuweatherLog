package com.example.tests;

import com.example.Location.AdministrativeArea;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AdminAreasTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(AdminAreasTest.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAdminAreas() {
        logger.info("Запуск теста testAdminAreas");

        // Мок-ответ для запроса
        String mockResponse = "[{\"CountryID\":\"RU\"}]";

        stubFor(get(urlPathEqualTo("/locations/v1/adminareas/RU"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Мок для /locations/v1/adminareas/RU создан");

        // Запрос к мок-серверу через RestAssured
        List<AdministrativeArea> areas = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/adminareas/RU")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", AdministrativeArea.class);

        assertFalse(areas.isEmpty());
        assertEquals("RU", areas.get(0).getCountryID());

        // Проверка, что мок-запрос был вызван
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/adminareas/RU"))
                .withQueryParam("apikey", equalTo(API_KEY)));
        logger.info("Тест testAdminAreas завершен успешно");
    }

    @Test
    public void testAdminAreasUnauthorized() {
        logger.info("Запуск теста testAdminAreasUnauthorized");

        // Мок-ответ для 401 Unauthorized
        stubFor(get(urlPathEqualTo("/locations/v1/adminareas/RU"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для /locations/v1/adminareas/RU с ошибкой 401 создан");

        // Отправляем запрос БЕЗ API-ключа
        given()
                .when()
                .get(getBaseUrl() + "/locations/v1/adminareas/RU")
                .then()
                .statusCode(401);

        // Проверка, что WireMock получил запрос без API-ключа
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/adminareas/RU")));
        logger.info("Тест testAdminAreasUnauthorized успешно завершен");
    }
}
