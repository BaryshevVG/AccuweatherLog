package com.example.tests;

import com.example.Location.Location;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class GeoLocationSearchTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(GeoLocationSearchTest.class);

    @Test
    public void testGeoLocationSearch() {
        logger.info("Запуск testGeoLocationSearch");

        String mockResponse = "{\"LocalizedName\": \"Tyumen\", \"Country\": {\"ID\": \"RU\"}}";

        stubFor(get(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("57.1522,65.5272"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /locations/v1/cities/geoposition/search");

        Location location = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "57.1522,65.5272")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/geoposition/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Location.class);

        assertNotNull(location);
        assertEquals("Tyumen", location.getLocalizedName());
        assertEquals("RU", location.getCountry().getId());

        logger.info("Тест testGeoLocationSearch завершен успешно");
    }

    @Test
    public void testGeoLocationSearchInvalidCoordinates() {
        logger.info("Запуск testGeoLocationSearchInvalidCoordinates");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("999,999"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Invalid coordinates\"}")));

        logger.debug("Создан мок для /locations/v1/cities/geoposition/search с некорректными координатами");

        given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "999,999")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/geoposition/search")
                .then()
                .statusCode(400);

        logger.info("Тест testGeoLocationSearchInvalidCoordinates завершен успешно");
    }
}
