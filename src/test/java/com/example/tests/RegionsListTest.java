package com.example.tests;

import com.example.Location.Region;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class RegionsListTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(RegionsListTest.class);

    @Test
    public void testRegionsList() {
        logger.info("Запуск testRegionsList");

        // Мокируем успешный ответ
        String mockResponse = "[{\"ID\": \"EU\"}, {\"ID\": \"NA\"}]";

        // Статика для WireMock
        stubFor(get(urlPathEqualTo("/locations/v1/regions"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для получения списка регионов");

        // Выполняем запрос через RestAssured
        List<Region> regions = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/regions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Region.class);

        // Проверяем, что список не пустой
        assertFalse(regions.isEmpty());
        assertNotNull(regions.get(0).getId());
        logger.info("Тест testRegionsList завершен успешно");
    }

    @Test
    public void testRegionsListNotFound() {
        logger.info("Запуск testRegionsListNotFound");

        // Мокируем ошибку с пустым результатом
        stubFor(get(urlPathEqualTo("/locations/v1/regions"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"No regions found\"}")));

        logger.debug("Создан мок для получения пустого списка регионов");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/regions")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
        logger.info("Тест testRegionsListNotFound завершен успешно");
    }
}
