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

public class AutocompleteSearchTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(AutocompleteSearchTest.class);

    @Test
    public void testAutocompleteSearch() {
        logger.info("Запуск теста testAutocompleteSearch");

        String mockResponse = "[{\"LocalizedName\": \"Tyumen\"}]";

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("Tyu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Мок для /locations/v1/cities/autocomplete создан");

        List<Location> cities = given()
                .queryParam("apikey", API_KEY)
                .queryParam("q", "Tyu")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Location.class);

        assertFalse(cities.isEmpty());
        assertTrue(cities.get(0).getLocalizedName().startsWith("Ty"));

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .withQueryParam("q", equalTo("Tyu")));

        logger.info("Тест testAutocompleteSearch завершен успешно");
    }

    @Test
    public void testAutocompleteSearchUnauthorized() {
        logger.info("Запуск теста testAutocompleteSearchUnauthorized");

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("Tyu"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для 401 Unauthorized создан");

        given()
                .queryParam("q", "Tyu")
                .when()
                .get(getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then()
                .statusCode(401);

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("Tyu")));

        logger.info("Тест testAutocompleteSearchUnauthorized завершен");
    }
}
