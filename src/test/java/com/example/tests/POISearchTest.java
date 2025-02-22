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

public class POISearchTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(POISearchTest.class);

    @Test
    public void testPOISearchInRussia() {
        logger.info("Запуск testPOISearchInRussia");

        // Мокируем успешный ответ
        stubFor(get(urlPathEqualTo("/locations/v1/poi/RU/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"Country\": {\"ID\": \"RU\"}}]")));

        // Выполняем запрос через RestAssured
        List<Location> pois = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "Moscow")
                .when()
                .get(getBaseUrl() + "/locations/v1/poi/RU/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        // Проверки
        assertFalse(pois.isEmpty(), "Список POI не должен быть пустым");
        assertNotNull(pois.get(0).getCountry(), "Поле Country не должно быть null");
        assertEquals("RU", pois.get(0).getCountry().getId(), "Неверный ID страны");

        logger.info("Тест testPOISearchInRussia успешно пройден");
    }

    @Test
    public void testPOISearchInvalidCountry() {
        logger.info("Запуск testPOISearchInvalidCountry");

        // Мокируем ответ с пустым результатом
        stubFor(get(urlPathEqualTo("/locations/v1/poi/XX/search"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        // Выполняем запрос и ожидаем пустой список
        List<Location> pois = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "NonExistentCity")
                .when()
                .get(getBaseUrl() + "/locations/v1/poi/XX/search")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        assertTrue(pois.isEmpty(), "Список POI должен быть пустым");

        logger.info("Тест testPOISearchInvalidCountry успешно пройден");
    }
}
