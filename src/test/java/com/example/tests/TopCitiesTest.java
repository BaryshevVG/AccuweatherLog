package com.example.tests;

import com.example.Location.Location;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TopCitiesTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(TopCitiesTest.class);

    @Test
    public void testTop50Cities() {
        logger.info("Запуск testTop50Cities");

        // Мокируем успешный ответ с 50 городами
        StringBuilder mockResponse = new StringBuilder("[");
        for (int i = 1; i <= 50; i++) {
            mockResponse.append("{\"EnglishName\": \"City " + i + "\", \"Country\": {\"ID\": \"ID" + i + "\", \"LocalizedName\": \"Country " + i + "\", \"EnglishName\": \"Country " + i + "\"}}");
            if (i < 50) {
                mockResponse.append(", ");
            }
        }
        mockResponse.append("]");

        // Статика для WireMock
        stubFor(get(urlPathEqualTo("/locations/v1/topcities/50"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse.toString())));

        logger.debug("Создан мок для получения 50 крупнейших городов");

        // Выполняем запрос через RestAssured
        List<Location> cities = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/topcities/50")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        // Проверяем, что результат имеет размер 50
        assertEquals(50, cities.size());
        logger.info("Тест testTop50Cities завершен успешно");
    }

    @Test
    public void testTop50CitiesNotFound() {
        logger.info("Запуск testTop50CitiesNotFound");

        // Мокируем ошибку с пустым результатом
        stubFor(get(urlPathEqualTo("/locations/v1/topcities/50"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Cities not found\"}")));

        logger.debug("Создан мок для запроса 50 крупнейших городов с ошибкой");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/topcities/50")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
        logger.info("Тест testTop50CitiesNotFound завершен успешно");
    }
}
