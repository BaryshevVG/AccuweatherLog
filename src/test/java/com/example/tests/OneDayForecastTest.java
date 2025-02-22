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

public class OneDayForecastTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(OneDayForecastTest.class);

    @Test
    public void testOneDayForecast() {
        logger.info("Запуск testOneDayForecast");

        // Мокируем успешный ответ
        String mockResponse = "{ \"DailyForecasts\": [{\"Temperature\": {\"Maximum\": {\"Value\": 25}}}] }";

        // Статика для WireMock, которая будет использоваться для имитации HTTP-запроса
        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/1day/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /forecasts/v1/daily/1day/295117");

        // Выполняем запрос через RestAssured
        List<DailyForecast> forecast = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/1day/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("DailyForecasts", DailyForecast.class);

        // Проверяем, что ответ не пустой и что в нем только 1 элемент
        assertNotNull(forecast);
        assertEquals(1, forecast.size());

        logger.info("Тест testOneDayForecast завершен успешно");
    }

    @Test
    public void testOneDayForecastNotFound() {
        logger.info("Запуск testOneDayForecastNotFound");

        // Мокируем ситуацию, когда прогноз не найден
        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/1day/000000"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not Found\"}")));

        logger.debug("Создан мок для /forecasts/v1/daily/1day/000000 с кодом 404");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .when()
                .get(getBaseUrl() + "/forecasts/v1/daily/1day/000000")
                .then()
                .statusCode(404);

        logger.info("Тест testOneDayForecastNotFound завершен успешно");
    }
}
