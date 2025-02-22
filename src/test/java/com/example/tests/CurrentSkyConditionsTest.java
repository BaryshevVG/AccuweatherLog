package com.example.tests;

import com.example.Weather.Headline;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CurrentSkyConditionsTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(CurrentSkyConditionsTest.class);

    @Test
    public void testCurrentSkyConditions() {
        logger.info("Запуск testCurrentSkyConditions");

        String mockResponse = "{\"Text\": \"Partly Cloudy\"}";

        stubFor(get(urlPathEqualTo("/currentconditions/v1/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /currentconditions/v1/295117");

        Headline skyConditions = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/currentconditions/v1/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Headline.class);

        assertNotNull(skyConditions);
        assertNotNull(skyConditions.getText());

        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/295117"))
                .withQueryParam("apikey", equalTo(API_KEY)));

        logger.info("Тест testCurrentSkyConditions завершен успешно");
    }

    @Test
    public void testCurrentSkyConditionsUnauthorized() {
        logger.info("Запуск теста testCurrentSkyConditionsUnauthorized");

        stubFor(get(urlPathEqualTo("/currentconditions/v1/295117"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для 401 Unauthorized создан");

        given()
                .when()
                .get(getBaseUrl() + "/currentconditions/v1/295117")
                .then()
                .statusCode(401);

        verify(getRequestedFor(urlPathEqualTo("/currentconditions/v1/295117")));
        logger.info("Тест testCurrentSkyConditionsUnauthorized завершен");
    }
}
