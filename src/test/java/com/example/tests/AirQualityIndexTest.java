package com.example.tests;

import com.example.Weather.Headline;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AirQualityIndexTest extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(AirQualityIndexTest.class);

    @Test
    public void testAirQualityIndex() {
        logger.info("Запуск теста testAirQualityIndex");

        String mockResponse = "{\"Text\": \"Good air quality\"}";

        stubFor(get(urlPathEqualTo("/airquality/v1/currentconditions/295117"))
                .withQueryParam("apikey", equalTo(API_KEY))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)));

        logger.debug("Мок для /airquality/v1/currentconditions/295117 создан");

        Headline airQuality = given()
                .queryParam("apikey", API_KEY)
                .when()
                .get(getBaseUrl() + "/airquality/v1/currentconditions/295117")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Headline.class);

        assertNotNull(airQuality);
        assertNotNull(airQuality.getText());

        verify(getRequestedFor(urlPathEqualTo("/airquality/v1/currentconditions/295117"))
                .withQueryParam("apikey", equalTo(API_KEY)));

        logger.info("Тест testAirQualityIndex завершен успешно");
    }

    @Test
    public void testAirQualityIndexUnauthorized() {
        logger.info("Запуск теста testAirQualityIndexUnauthorized");

        stubFor(get(urlPathEqualTo("/airquality/v1/currentconditions/295117"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Unauthorized\"}")));

        logger.debug("Мок для 401 Unauthorized создан");

        given()
                .when()
                .get(getBaseUrl() + "/airquality/v1/currentconditions/295117")
                .then()
                .statusCode(401);

        verify(getRequestedFor(urlPathEqualTo("/airquality/v1/currentconditions/295117")));
        logger.info("Тест testAirQualityIndexUnauthorized завершен");
    }
}
