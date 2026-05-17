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
import java.util.UUID;
import Entities.Product;
import helpers.AuthHelper;
import helpers.EnvConfig;

@Feature("Produtos")
public class ProductTest extends BaseTest {

  private static String productId;
  private static RequestSpecification authRequestSpec;

  @BeforeAll
  static void setupProduct() {
    authRequestSpec = AuthHelper.buildAuthSpec(
        requestSpec,
        EnvConfig.get("user.email"),
        EnvConfig.get("user.password")
    );

    productId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          100,
          "Description test",
          10
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");
  }

  @AfterAll
  static void cleanupProduct() {
    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + productId)
    .then()
      .statusCode(200);
  }

  @Test
  @Story("Listar produtos")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /produtos - deve retornar lista de produtos")
  public void getProducts() {
    given(requestSpec)
    .when()
      .get("/produtos")
    .then()
      .statusCode(200)
      .body("quantidade", greaterThan(0))
      .body("produtos", not(empty()));
  }

  @Test
  @Story("Criar produto")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("POST /produtos - deve criar produto e retornar ID")
  public void createProduct() {
    String newProductId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          150,
          "Description create test",
          5
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .body("message", equalTo("Cadastro realizado com sucesso"))
      .body("_id", notNullValue())
      .extract()
      .jsonPath()
      .getString("_id");

    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + newProductId)
    .then()
      .statusCode(200);
  }

  @Test
  @Story("Buscar produto")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /produtos/{id} - deve retornar produto pelo ID")
  public void getProductById() {
    given(requestSpec)
    .when()
      .get("/produtos/" + productId)
    .then()
      .statusCode(200)
      .body("_id", equalTo(productId));
  }

  @Test
  @Story("Atualizar produto")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("PUT /produtos/{id} - deve atualizar os dados do produto")
  public void updateProduct() {
    given().spec(authRequestSpec)
      .body(new Product(
          "Product_Updated_" + UUID.randomUUID().toString().substring(0, 8),
          200,
          "Updated description",
          20
      ))
    .when()
      .put("/produtos/" + productId)
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro alterado com sucesso"));
  }

  @Test
  @Story("Excluir produto")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("DELETE /produtos/{id} - deve excluir o produto com sucesso")
  public void deleteProduct() {
    String tempProductId = given().spec(authRequestSpec)
      .body(new Product(
          "Product_" + UUID.randomUUID().toString().substring(0, 8),
          50,
          "Temp description",
          5
      ))
    .when()
      .post("/produtos")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");

    given().spec(authRequestSpec)
    .when()
      .delete("/produtos/" + tempProductId)
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro excluído com sucesso"));
  }

  @Test
  @Story("Buscar produto")
  @Severity(SeverityLevel.MINOR)
  @DisplayName("GET /produtos/{id} - deve retornar 400 para ID inexistente")
  public void getProductByIdNotFound() {
    String nonExistentId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

    given(requestSpec)
    .when()
      .get("/produtos/" + nonExistentId)
    .then()
      .statusCode(400)
      .body("message", equalTo("Produto não encontrado"));
  }
}