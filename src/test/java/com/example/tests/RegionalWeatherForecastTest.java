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

public class RegionalWeatherForecastTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(RegionalWeatherForecastTest.class);

    @Test
    public void testRegionalWeatherForecast() {
        logger.info("Запуск testRegionalWeatherForecast");

        // Мокируем успешный ответ
        String mockResponse = "{\"DailyForecasts\": [{\"Temperature\": {\"Maximum\": {\"Value\": 25}}}]}";

        // Статика для WireMock
        stubFor(get(urlPathEqualTo("/forecasts/v1/regional/5day/EUR"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для прогноза погоды для региона");

        // Выполняем запрос через RestAssured
        List<DailyForecast> regionalForecast = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/regional/5day/EUR")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("DailyForecasts", DailyForecast.class);

        // Проверяем, что результат не пустой
        assertNotNull(regionalForecast);
        assertFalse(regionalForecast.isEmpty());
        logger.info("Тест testRegionalWeatherForecast завершен успешно");
    }

    @Test
    public void testRegionalWeatherForecastInvalidRegion() {
        logger.info("Запуск testRegionalWeatherForecastInvalidRegion");

        // Мокируем ошибку с неверным регионом
        stubFor(get(urlPathEqualTo("/forecasts/v1/regional/5day/INVALID"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Invalid region code\"}")));

        logger.debug("Создан мок для запроса с неверным регионом");

        // Выполняем запрос и ожидаем ошибку 400
        given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/regional/5day/INVALID")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);
        logger.info("Тест testRegionalWeatherForecastInvalidRegion завершен успешно");
    }
}
