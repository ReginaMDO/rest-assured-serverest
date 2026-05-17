package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.UUID;
import Entities.User;

@Feature("Usuários")
public class UserTest extends BaseTest {
  
  private static String userId;

  @BeforeAll
  static void setupUser() {
    User user = new User(
        "Fulano_" + UUID.randomUUID().toString().substring(0, 8),
        "fulano_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com.br",
        "test1234",
        "true"
    );

    userId = given(requestSpec)
      .body(user)
    .when()
      .post("/usuarios")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");
  }

  @AfterAll
  static void cleanupUser() {
    given(requestSpec)
    .when()
      .delete("/usuarios/" + userId)
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro excluído com sucesso"));
  }

  @Test
  @Story("Listar usuários")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /usuarios - deve retornar lista de usuários")
  public void getUsers() {
    given(requestSpec)
    .when()
      .get("/usuarios")
    .then()
      .statusCode(200)
      .body("quantidade", greaterThan(0))
      .body("usuarios", not(empty()));
  }

  @Test
  @Story("Criar usuário")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("POST /usuarios - deve criar usuário e retornar ID")
  public void createUser() {
    User user = new User(
        "Fulano_" + UUID.randomUUID().toString().substring(0, 8),
        "fulano_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com.br",
        "test1234",
        "true"
    );

    String newUserId = given(requestSpec)
      .body(user)
    .when()
      .post("/usuarios")
    .then()
      .statusCode(201)
      .body("message", equalTo("Cadastro realizado com sucesso"))
      .body("_id", notNullValue())
      .extract()
      .jsonPath()
      .getString("_id");

    given(requestSpec)
    .when()
      .delete("/usuarios/" + newUserId)
    .then()
      .statusCode(200);
  }

  @Test
  @Story("Buscar usuário")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("GET /usuarios/{id} - deve retornar usuário pelo ID")
  public void getUserById() {
    given(requestSpec)
    .when()
      .get("/usuarios/" + userId)
    .then()
      .statusCode(200)
      .body("_id", equalTo(userId));
  }

  @Test
  @Story("Atualizar usuário")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("PUT /usuarios/{id} - deve atualizar os dados do usuário")
  public void updateUser() {
    User user = new User(
        "Fulano_Updated_" + UUID.randomUUID().toString().substring(0, 8),
        "fulano_updated_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com.br",
        "test1234",
        "true"
    );

    given(requestSpec)
      .body(user)
    .when()
      .put("/usuarios/" + userId)
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro alterado com sucesso"));
  }

  @Test
  @Story("Excluir usuário")
  @Severity(SeverityLevel.NORMAL)
  @DisplayName("DELETE /usuarios/{id} - deve excluir o usuário com sucesso")
  public void deleteUser() {
    String tempUserId = given(requestSpec)
      .body(new User(
          "Fulano_" + UUID.randomUUID().toString().substring(0, 8),
          "fulano_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com.br",
          "test1234",
          "true"
      ))
    .when()
      .post("/usuarios")
    .then()
      .statusCode(201)
      .extract()
      .jsonPath()
      .getString("_id");

    given(requestSpec)
    .when()
      .delete("/usuarios/" + tempUserId)
    .then()
      .statusCode(200)
      .body("message", equalTo("Registro excluído com sucesso"));
  }

  @Test
  @Story("Criar usuário")
  @Severity(SeverityLevel.MINOR)
  @DisplayName("POST /usuarios - deve retornar 400 ao criar usuário com email inválido")
  public void createUserWithInvalidEmail() {
    given(requestSpec)
      .body(new User("Fulano", "email-invalido", "test1234", "true"))
    .when()
      .post("/usuarios")
    .then()
      .statusCode(400)
      .body("email", equalTo("email deve ser um email válido"));
  }

  @Test
  @Story("Criar usuário")
  @Severity(SeverityLevel.MINOR)
  @DisplayName("POST /usuarios - deve retornar 400 ao criar usuário com email já cadastrado")
  public void createUserWithDuplicatedEmail() {
    given(requestSpec)
      .body(new User("Fulano", "fulano@qa.com", "teste", "true"))
    .when()
      .post("/usuarios")
    .then()
      .statusCode(400)
      .body("message", equalTo("Este email já está sendo usado"));
  }

  @Test
  @Story("Buscar usuário")
  @Severity(SeverityLevel.MINOR)
  @DisplayName("GET /usuarios/{id} - deve retornar 400 ao buscar usuário com ID inexistente")
  public void getUserByIdNotFound() {
    String idInexistente = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

    given(requestSpec)
    .when()
      .get("/usuarios/" + idInexistente)
    .then()
      .statusCode(400)
      .body("message", equalTo("Usuário não encontrado"));
  }

}