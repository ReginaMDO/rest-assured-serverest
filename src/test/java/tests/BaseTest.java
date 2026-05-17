package tests;

import helpers.EnvConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import static io.restassured.RestAssured.given;

public abstract class BaseTest {

  protected static RequestSpecification requestSpec;

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = EnvConfig.get("base.url");
    RestAssured.filters(new AllureRestAssured());
    requestSpec = given()
        .contentType(ContentType.JSON)
        .log().ifValidationFails();
  }
}
