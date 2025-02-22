package com.example.tests;

import com.example.Weather.Headline;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CurrentWeatherTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(CurrentWeatherTest.class);

    @Test
    public void testCurrentWeather() {
        logger.info("Запуск testCurrentWeather");

        String mockResponse = "{\"Text\": \"Sunny\"}";

        stubFor(get(urlPathEqualTo("/currentconditions/v1/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /currentconditions/v1/295117");

        Headline weather = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/currentconditions/v1/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Headline.class);

        assertNotNull(weather);
        assertNotNull(weather.getText());
        assertEquals("Sunny", weather.getText());

        logger.info("Тест testCurrentWeather завершен успешно");
    }

    @Test
    public void testCurrentWeatherUnauthorized() {
        logger.info("Запуск testCurrentWeatherUnauthorized");

        stubFor(get(urlPathEqualTo("/currentconditions/v1/295117"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Создан мок для /currentconditions/v1/295117 с кодом 401");

        given()
                .when()
                .get(getBaseUrl() + "/currentconditions/v1/295117")
                .then()
                .statusCode(401);

        logger.info("Тест testCurrentWeatherUnauthorized завершен успешно");
    }
}
