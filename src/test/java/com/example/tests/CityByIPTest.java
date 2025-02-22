package com.example.tests;

import com.example.Location.Location;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CityByIPTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(CityByIPTest.class);

    @Test
    public void testFindCityByIP() {
        logger.info("Запуск теста testFindCityByIP");

        String mockResponse = "{\"LocalizedName\": \"London\", \"Country\": {\"ID\": \"GB\"}}";

        stubFor(get(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("2.59.241.32"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Мок для /locations/v1/cities/ipaddress создан");

        Location location = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "2.59.241.32")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/ipaddress")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Location.class);

        assertNotNull(location.getLocalizedName());
        assertNotNull(location.getCountry().getId());

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("2.59.241.32")));

        logger.info("Тест testFindCityByIP завершен успешно");
    }

    @Test
    public void testFindCityByIPUnauthorized() {
        logger.info("Запуск теста testFindCityByIPUnauthorized");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("q", equalTo("2.59.241.32"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для 401 Unauthorized создан");

        given()
                .queryParam("q", "2.59.241.32")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/ipaddress")
                .then()
                .statusCode(401);

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("q", equalTo("2.59.241.32")));

        logger.info("Тест testFindCityByIPUnauthorized завершен");
    }
}
