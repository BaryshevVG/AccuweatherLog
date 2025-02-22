package com.example.tests;

import com.example.Weather.DailyForecast;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class FiveDayForecastTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(FiveDayForecastTest.class);

    @Test
    public void testFiveDayForecast() {
        logger.info("Запуск testFiveDayForecast");

        String mockResponse = "{ \"DailyForecasts\": [{}, {}, {}, {}, {}] }";

        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/5day/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /forecasts/v1/daily/5day/295117");

        List<DailyForecast> forecast = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/5day/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("DailyForecasts", DailyForecast.class);

        assertNotNull(forecast);
        assertEquals(5, forecast.size());

        logger.info("Тест testFiveDayForecast завершен успешно");
    }

    @Test
    public void testFiveDayForecastUnauthorized() {
        logger.info("Запуск testFiveDayForecastUnauthorized");

        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/5day/295117"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Создан мок для /forecasts/v1/daily/5day/295117 с кодом 401");

        given()
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/5day/295117")
                .then()
                .statusCode(401);

        logger.info("Тест testFiveDayForecastUnauthorized завершен успешно");
    }
}
