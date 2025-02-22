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

public class NeighboringCitiesTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(NeighboringCitiesTest.class);

    @Test
    public void testNeighboringCities() {
        logger.info("Запуск testNeighboringCities");

        String mockResponse = "[{\"LocalizedName\": \"Omsk\"}, {\"LocalizedName\": \"Kurgan\"}]";

        stubFor(get(urlPathEqualTo("/locations/v1/cities/neighbors/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /locations/v1/cities/neighbors/295117");

        List<Location> cities = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/neighbors/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        assertFalse(cities.isEmpty());
        assertNotNull(cities.get(0).getLocalizedName());

        logger.info("Тест testNeighboringCities завершен успешно");
    }

    @Test
    public void testNeighboringCitiesNotFound() {
        logger.info("Запуск testNeighboringCitiesNotFound");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/neighbors/000000"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not Found\"}")));

        logger.debug("Создан мок для /locations/v1/cities/neighbors/000000 с кодом 404");

        given()
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/neighbors/000000")
                .then()
                .statusCode(404);

        logger.info("Тест testNeighboringCitiesNotFound завершен успешно");
    }
}
