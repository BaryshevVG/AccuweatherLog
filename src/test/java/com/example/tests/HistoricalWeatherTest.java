package com.example.tests;

import com.example.Weather.Headline;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class HistoricalWeatherTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(HistoricalWeatherTest.class);

    @Test
    public void testHistoricalWeather() {
        logger.info("Запуск testHistoricalWeather");

        String mockResponse = "{\"Text\": \"Cloudy\"}";

        stubFor(get(urlPathEqualTo("/historical/v1/295117/24hour"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /historical/v1/295117/24hour");

        Headline historicalWeather = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/historical/v1/295117/24hour")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Headline.class);

        assertNotNull(historicalWeather);
        assertEquals("Cloudy", historicalWeather.getText());

        logger.info("Тест testHistoricalWeather завершен успешно");
    }

    @Test
    public void testHistoricalWeatherNotFound() {
        logger.info("Запуск testHistoricalWeatherNotFound");

        stubFor(get(urlPathEqualTo("/historical/v1/000000/24hour"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not Found\"}")));

        logger.debug("Создан мок для /historical/v1/000000/24hour с кодом 404");

        given()
                .when()
                .get(getBaseUrl() + "/historical/v1/000000/24hour")
                .then()
                .statusCode(404);

        logger.info("Тест testHistoricalWeatherNotFound завершен успешно");
    }
}
