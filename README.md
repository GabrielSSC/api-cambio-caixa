# API de Câmbio USD/EUR

API REST para cadastro de clientes, consulta de cotações de dólar e euro e registro de ordens de compra de moeda estrangeira para retirada em agência.

## Tecnologias

- Java 25
- Spring Boot 3.5
- Spring Web, Validation, Security e Data JPA
- H2 em memória como banco padrão
- Flyway para versionamento do esquema
- AwesomeAPI para consulta de cotações
- JUnit, Mockito e Spring Boot Test

## Pré-requisitos

- JDK 25
- Maven 3.9 ou compatível
- Acesso à internet para consultar a AwesomeAPI

Confirme que `java -version` e `mvn -version` apontam para o JDK 25.

## Executar

Na raiz do projeto, defina a senha da API como variável de ambiente e inicie a aplicação:

```powershell
$env:APP_SECURITY_PASSWORD = Read-Host "Defina a senha da API"
mvn spring-boot:run
```

Em Bash, use `export APP_SECURITY_PASSWORD='sua-senha'` antes de executar `mvn spring-boot:run`. A variável precisa estar definida no mesmo terminal em que a aplicação é iniciada. A aplicação não inicia se ela estiver ausente.

A API inicia em `http://localhost:8081`. Por padrão, usa um banco H2 em memória; o Flyway cria o esquema na inicialização e os dados são perdidos quando a aplicação é encerrada.

As configurações atuais estão em `src/main/resources/application.properties`. O arquivo `docker-compose.yml` disponibiliza um PostgreSQL, mas a aplicação está configurada para usar H2 por padrão.

## Autenticação

Os endpoints de negócio exigem HTTP Basic:

- Usuário: `instructor`
- Senha: valor definido em `APP_SECURITY_PASSWORD` no ambiente

O segredo não fica no código nem em `application.properties`: é fornecido pelo ambiente na inicialização e codificado com BCrypt antes de ser usado pelo Spring Security. Essa escolha mantém credenciais fora do repositório e facilita configurar valores diferentes por ambiente. Para apresentar o projeto, explique que a senha deve ser definida no terminal antes de iniciar a aplicação. Não reutilize credenciais reais ou de produção. A documentação Swagger e o JSON OpenAPI são públicos; as chamadas de negócio feitas pela interface Swagger continuam exigindo autenticação.

Se uma credencial já tiver sido versionada, removê-la do código não apaga o histórico do Git; troque-a e configure um novo valor no ambiente.

## Documentação da API

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

Na interface Swagger, use **Authorize** e informe o usuário `instructor` e a senha configurada no ambiente para testar os endpoints.

## Endpoints

| Método | Endpoint | Descrição | Respostas principais |
|---|---|---|---|
| `POST` | `/api/clientes` | Cadastra um cliente (UC1) | `201`, `400`, `409` |
| `GET` | `/api/clientes/{cpf}` | Consulta cliente por CPF (UC2) | `200`, `404` |
| `GET` | `/api/cambio/cotacao/{moeda}` | Consulta cotação de `USD` ou `EUR` (UC3) | `200`, `422`, `503` |
| `POST` | `/api/compras` | Registra uma ordem de compra (UC4) | `201`, `400`, `404`, `422`, `503` |
| `GET` | `/api/compras/{id}` | Consulta uma ordem pelo identificador | `200`, `404` |
| `GET` | `/api/compras/cliente/{cpf}` | Consulta o histórico do cliente (UC5) | `200` |

### Cadastrar cliente

O CPF pode ser informado com ou sem pontuação. `estadoCivil` e `sexo` devem usar os valores dos enums.

```bash
curl -u "instructor:${APP_SECURITY_PASSWORD}" -X POST http://localhost:8081/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Souza","cpf":"43488428095","dataNascimento":"1990-05-15","estadoCivil":"SOLTEIRO","sexo":"FEMININO"}'
```

### Consultar cotação

```bash
curl -u "instructor:${APP_SECURITY_PASSWORD}" \
  http://localhost:8081/api/cambio/cotacao/USD
```

Use `EUR` no lugar de `USD` para consultar o euro. Moedas diferentes de USD e EUR retornam `422`; indisponibilidade da API externa retorna `503`.

### Registrar ordem de compra

Cadastre o cliente antes de registrar uma ordem. O CPF deve conter 11 dígitos e o número da agência, quatro dígitos.

```bash
curl -u "instructor:${APP_SECURITY_PASSWORD}" -X POST http://localhost:8081/api/compras \
  -H "Content-Type: application/json" \
  -d '{"cpf":"43488428095","tipoMoeda":"EUR","valorMoedaEstrangeira":100.00,"numeroAgenciaRetirada":"7057"}'
```

A resposta `201 Created` inclui os identificadores do cliente e da compra, a cotação consultada e o valor total calculado.

## Testes

```bash
mvn test -Dmaven.test.skip=false
```

## Organização e decisões técnicas

A aplicação é um monólito modular, organizado por responsabilidade:

- `cliente`: cadastro, consulta e persistência de clientes.
- `cambio`: integração com a AwesomeAPI e consulta de cotação.
- `compra`: registro, consulta e histórico de ordens.
- `config`, `exception` e `web`: configurações transversais, exceções de negócio e respostas de erro.

O fluxo de compra é orquestrado por `OrdemCompraService`: consulta o cliente, obtém a cotação, calcula o total e persiste a ordem. A integração externa é isolada em `AwesomeApiClient`, que adapta a resposta da AwesomeAPI ao modelo interno da aplicação. Os serviços recebem suas dependências por construtor.

O esquema do banco é gerenciado pelas migrations em `src/main/resources/db/migration`. As exceções de negócio são convertidas em respostas HTTP consistentes por `GlobalExceptionHandler`.
