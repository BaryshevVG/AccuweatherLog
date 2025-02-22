package com.example.tests;

import com.example.Location.Location;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CityDetailsByCoordinatesTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(CityDetailsByCoordinatesTest.class);

    @Test
    public void testCityDetailsByCoordinates() {
        logger.info("Запуск теста testCityDetailsByCoordinates");

        String mockResponse = "{\"EnglishName\": \"Moscow\", \"Country\": {\"ID\": \"RU\"}}";

        stubFor(get(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("55.751244,37.618423"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Мок для /locations/v1/cities/geoposition/search создан");

        Location location = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "55.751244,37.618423")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/geoposition/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Location.class);

        assertEquals("Moscow", location.getEnglishName());
        assertEquals("RU", location.getCountry().getId());

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("55.751244,37.618423")));

        logger.info("Тест testCityDetailsByCoordinates завершен успешно");
    }

    @Test
    public void testCityDetailsByCoordinatesUnauthorized() {
        logger.info("Запуск теста testCityDetailsByCoordinatesUnauthorized");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("q", equalTo("55.751244,37.618423"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для 401 Unauthorized создан");

        given()
                .queryParam("q", "55.751244,37.618423")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/geoposition/search")
                .then()
                .statusCode(401);

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("q", equalTo("55.751244,37.618423")));

        logger.info("Тест testCityDetailsByCoordinatesUnauthorized завершен");
    }
}
