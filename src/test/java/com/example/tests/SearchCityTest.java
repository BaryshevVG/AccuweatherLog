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

public class SearchCityTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchCityTest.class);

    @Test
    public void testSearchCity() {
        logger.info("Запуск testSearchCity");

        // Мокируем успешный ответ
        String mockResponse = "[{\"EnglishName\": \"Moscow\", \"Country\": {\"ID\": \"RU\"}}]";

        // Статика для WireMock
        stubFor(get(urlPathEqualTo("/locations/v1/cities/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("Moscow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для поиска города");

        // Выполняем запрос через RestAssured
        List<Location> cities = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "Moscow")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        // Проверяем, что результат не пустой и соответствует ожидаемым данным
        assertFalse(cities.isEmpty());
        assertEquals("Moscow", cities.get(0).getEnglishName());
        assertEquals("RU", cities.get(0).getCountry().getId());  // Теперь используем getID() для доступа к полю
        logger.info("Тест testSearchCity завершен успешно");
    }

    @Test
    public void testSearchCityNotFound() {
        logger.info("Запуск testSearchCityNotFound");

        // Мокируем ошибку с пустым результатом
        stubFor(get(urlPathEqualTo("/locations/v1/cities/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("NonExistentCity"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"City not found\"}")));

        logger.debug("Создан мок для запроса города, которого нет");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "NonExistentCity")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/search")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
        logger.info("Тест testSearchCityNotFound завершен успешно");
    }
}
