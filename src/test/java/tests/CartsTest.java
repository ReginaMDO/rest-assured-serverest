package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.UUID;
import Entities.Carts;
import Entities.Carts.CartItem;
import Entities.Product;
import Entities.User;
import helpers.AuthHelper;
import helpers.EnvConfig;

@Feature("Carrinhos")
public class CartsTest extends BaseTest {

  private static String cartId;
  private static String productId;
  private static RequestSpecification authRequestSpec;

  @BeforeAll
  static void setupCart() {
    authRequestSpec = AuthHelper.buildAuthSpec(
        requestSpec,
        EnvConfig.get("user.email"),
        EnvConfig.get("user.password")
    );

    given().spec(authRequestSpec)
    .when()
      .delete("/carrinhos/cancelar-compra");

    productId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          100,
          "Description cart test",
          10
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");

    cartId = given().spec(authRequestSpec)
      .body(new Carts(List.of(new CartItem(productId, 1))))
    .when()
      .post("/carrinhos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");
  }

  @AfterAll
  static void cleanupCart() {
    given().spec(authRequestSpec)
    .when()
      .delete("/carrinhos/cancelar-compra")
    .then()
      .statusCode(200);

    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + productId)
    .then()
      .statusCode(200);
  }

  @Test
  @Story("Listar carrinhos")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /carrinhos - deve retornar lista de carrinhos")
  public void getCarts() {
    given(requestSpec)
    .when()
      .get("/carrinhos")
    .then()
      .statusCode(200)
      .body("quantidade", greaterThan(0))
      .body("carrinhos", not(empty()));
  }

  @Test
  @Story("Buscar carrinho")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /carrinhos/{id} - deve retornar carrinho pelo ID")
  public void getCartById() {
    given(requestSpec)
    .when()
      .get("/carrinhos/" + cartId)
    .then()
      .statusCode(200)
      .body("_id", equalTo(cartId));
  }

  @Test
  @Story("Buscar carrinho")
  @Severity(SeverityLevel.MINOR)
  @DisplayName("GET /carrinhos/{id} - deve retornar 400 para ID inexistente")
  public void getCartByIdNotFound() {
    String nonExistentId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

    given(requestSpec)
    .when()
      .get("/carrinhos/" + nonExistentId)
    .then()
      .statusCode(400)
      .body("message", equalTo("Carrinho não encontrado"));
  }

  @Test
  @Story("Concluir compra")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("DELETE /carrinhos/concluir-compra - deve concluir a compra e excluir o carrinho")
  public void concludePurchase() {
    String tempProductId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          50,
          "Temp product for conclude",
          5
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");

    String tempEmail = "temp_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com";
    String tempPassword = "teste";
    String tempUserId = given(requestSpec)
      .body(new User("Temp User", tempEmail, tempPassword, "true"))
    .when()
      .post("/usuarios")
    .then()
      .statusCode(201)
      .extract().jsonPath().getString("_id");

    RequestSpecification tempAuthSpec = AuthHelper.buildAuthSpec(requestSpec, tempEmail, tempPassword);

    given().spec(tempAuthSpec)
      .body(new Carts(List.of(new CartItem(tempProductId, 1))))
    .when()
      .post("/carrinhos")
    .then()
      .statusCode(201);

    given().spec(tempAuthSpec)
    .when()
      .delete("/carrinhos/concluir-compra")
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro excluído com sucesso"));

    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + tempProductId)
    .then()
      .statusCode(200);

    given(requestSpec)
    .when()
      .delete("/usuarios/" + tempUserId)
    .then()
      .statusCode(200);
  }

  @Test
  @Story("Cancelar compra")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("DELETE /carrinhos/cancelar-compra - deve cancelar a compra e restituir estoque")
  public void cancelPurchase() {
    String tempProductId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          50,
          "Temp product for cancel",
          5
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");

    String tempEmail = "temp_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com";
    String tempPassword = "teste";
    String tempUserId = given(requestSpec)
      .body(new User("Temp User", tempEmail, tempPassword, "true"))
    .when()
      .post("/usuarios")
    .then()
      .statusCode(201)
      .extract().jsonPath().getString("_id");

    RequestSpecification tempAuthSpec = AuthHelper.buildAuthSpec(requestSpec, tempEmail, tempPassword);

    given().spec(tempAuthSpec)
      .body(new Carts(List.of(new CartItem(tempProductId, 1))))
    .when()
      .post("/carrinhos")
    .then()
      .statusCode(201);

    given().spec(tempAuthSpec)
    .when()
      .delete("/carrinhos/cancelar-compra")
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro excluído com sucesso. Estoque dos produtos reabastecido"));

    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + tempProductId)
    .then()
      .statusCode(200);

    given(requestSpec)
    .when()
      .delete("/usuarios/" + tempUserId)
    .then()
      .statusCode(200);
  }
}