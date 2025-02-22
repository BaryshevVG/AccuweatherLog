package com.example.tests;

import com.example.Weather.Temperature;
import com.example.Weather.Minimum;
import com.example.Weather.Maximum;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class HourlyForecastTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(HourlyForecastTest.class);

    @Test
    public void testHourlyForecast() {
        logger.info("Запуск testHourlyForecast");

        // Мокируем успешный ответ для прогноза по часам
        String mockResponse = "{ \"HourlyForecasts\": [{" +
                "\"Temperature\": {\"Minimum\": {\"Value\": 23}, \"Maximum\": {\"Value\": 25}}}, " +
                "{\"Temperature\": {\"Minimum\": {\"Value\": 24}, \"Maximum\": {\"Value\": 26}}}" +
                "]}";

        // Статика для WireMock, которая будет использоваться для имитации HTTP-запроса
        stubFor(get(urlPathEqualTo("/forecasts/v1/hourly/12hour/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Создан мок для /forecasts/v1/hourly/12hour/295117");

        // Выполняем запрос через RestAssured
        List<Temperature> hourlyForecast = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/forecasts/v1/hourly/12hour/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("HourlyForecasts.Temperature", Temperature.class);

        // Проверяем, что ответ не пустой
        assertNotNull(hourlyForecast);
        assertFalse(hourlyForecast.isEmpty());

        // Проверяем значения температур Minimum и Maximum
        assertEquals(2, hourlyForecast.size()); // Два значения температуры в мок-ответе

        Temperature firstForecast = hourlyForecast.get(0);
        assertNotNull(firstForecast.getMinimum());
        assertNotNull(firstForecast.getMaximum());
        assertEquals(23, firstForecast.getMinimum().getValue());
        assertEquals(25, firstForecast.getMaximum().getValue());

        Temperature secondForecast = hourlyForecast.get(1);
        assertNotNull(secondForecast.getMinimum());
        assertNotNull(secondForecast.getMaximum());
        assertEquals(24, secondForecast.getMinimum().getValue());
        assertEquals(26, secondForecast.getMaximum().getValue());

        logger.info("Тест testHourlyForecast завершен успешно");
    }

    @Test
    public void testHourlyForecastNotFound() {
        logger.info("Запуск testHourlyForecastNotFound");

        // Мокируем ситуацию, когда прогноз по часам не найден
        stubFor(get(urlPathEqualTo("/forecasts/v1/hourly/12hour/000000"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not Found\"}")));

        logger.debug("Создан мок для /forecasts/v1/hourly/12hour/000000 с кодом 404");

        // Выполняем запрос и ожидаем ошибку 404
        given()
                .when()
                .get(getBaseUrl() + "/forecasts/v1/hourly/12hour/000000")
                .then()
                .statusCode(404);

        logger.info("Тест testHourlyForecastNotFound завершен успешно");
    }
}
