package com.example.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    protected static final String BASE_URL = "http://dataservice.accuweather.com";
    protected static final String API_KEY = "VRoAXaE4746JklJJ8EG6af9kwBQqrCQa";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
    }
}
