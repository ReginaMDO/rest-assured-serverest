# rest-assured-serverest

Projeto de testes de API REST para a aplicação [ServeRest](https://serverest.dev), desenvolvido com Java 21, REST Assured e JUnit 5.

## Tecnologias

- Java 21
- REST Assured 5.4.0
- JUnit Jupiter 5.11.0
- Allure Report 2.27.0
- Jackson Databind 2.17.0
- Maven 3

## Estrutura do projeto

```
src/
├── main/java/Entities/
│   ├── Carts.java
│   ├── Product.java
│   └── User.java
└── test/
    ├── java/
    │   ├── helpers/
    │   │   ├── AuthHelper.java       # Encapsula login e geração do authRequestSpec
    │   │   └── EnvConfig.java        # Resolução de configuração via variáveis de ambiente
    │   └── tests/
    │       ├── BaseTest.java         # Configuração base compartilhada por todos os testes
    │       ├── CartsTest.java        # Testes do endpoint /carrinhos
    │       ├── ProductTest.java      # Testes do endpoint /produtos
    │       └── UserTest.java         # Testes do endpoint /usuarios
    └── resources/
        ├── config.properties.example
        └── junit-platform.properties
.github/workflows/tests.yml           # Pipeline CI/CD com GitHub Actions
```

## Cobertura de testes

| Endpoint         | Cenários                                              |
|------------------|-------------------------------------------------------|
| `GET /usuarios`         | Lista usuários                                 |
| `POST /usuarios`        | Cria usuário, email inválido, email duplicado  |
| `GET /usuarios/{id}`    | Busca por ID, ID inexistente                   |
| `PUT /usuarios/{id}`    | Atualiza usuário                               |
| `DELETE /usuarios/{id}` | Exclui usuário                                 |
| `GET /produtos`         | Lista produtos                                 |
| `POST /produtos`        | Cria produto (autenticado)                     |
| `GET /produtos/{id}`    | Busca por ID, ID inexistente                   |
| `PUT /produtos/{id}`    | Atualiza produto (autenticado)                 |
| `DELETE /produtos/{id}` | Exclui produto (autenticado)                   |
| `GET /carrinhos`        | Lista carrinhos                                |
| `POST /carrinhos`       | Cria carrinho (autenticado)                    |
| `GET /carrinhos/{id}`   | Busca por ID, ID inexistente                   |
| `DELETE /carrinhos/concluir-compra` | Conclui compra (autenticado)    |
| `DELETE /carrinhos/cancelar-compra` | Cancela compra (autenticado)    |

## Como executar localmente

**1. Clone o repositório**
```bash
git clone https://github.com/<seu-usuario>/rest-assured-serverest.git
cd rest-assured-serverest
```

**2. Defina as variáveis de ambiente**

PowerShell:
```powershell
$env:BASE_URL      = "https://serverest.dev"
$env:USER_EMAIL    = "teste@qa.com"
$env:USER_PASSWORD = "1234"
```

Linux/macOS:
```bash
export BASE_URL="https://serverest.dev"
export USER_EMAIL="teste@qa.com"
export USER_PASSWORD="1234"
```

**3. Execute os testes**
```bash
mvn test
```

**4. Gere e visualize o relatório Allure**
```bash
mvn allure:serve
```

## CI/CD

O pipeline é executado automaticamente a cada `push` ou `pull request` para `main`/`master`.

Configure os seguintes secrets no repositório:
> **Settings → Secrets and variables → Actions → New repository secret**

- `BASE_URL`
- `USER_EMAIL`
- `USER_PASSWORD`

O relatório Allure é gerado e publicado automaticamente no **GitHub Pages** após cada execução.
