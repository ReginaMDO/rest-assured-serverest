package helpers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class AuthHelper {

    public static RequestSpecification buildAuthSpec(RequestSpecification baseSpec, String email, String password) {
        String token = given(baseSpec)
            .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("authorization");

        return new RequestSpecBuilder()
            .addRequestSpecification(baseSpec)
            .addHeader("Authorization", token)
            .build();
    }
}
