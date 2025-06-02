#   ComUnidade - Global Solution FIAP 
## 👥 Integrantes

- **Nome:** Guilherme Francisco   
  **RM:** 554678 
- **Nome:** Larissa de Freitas
  **RM:** 555136
- **Nome:** João Victor Rebello de Santis  
  **RM:** 555287
  ## 📺 Link para o Vídeo de Demonstração no YouTube

(https://www.youtube.com/watch?v=2EtoJaAlyH8)

## 🎤 Link para o Vídeo Pitch no YouTube (se aplicável)

[LINK_VIDEO_PITCH_3_MINUTOS]

## 💡 Descrição da API Backend "ComUnidade"

Esta API RESTful, desenvolvida com Java Spring Boot, serve como o backend para a aplicação "ComUnidade". Ela é responsável por gerir os dados centrais da aplicação, como os boletins de alerta e informação, e por fornecer a lógica de negócios e segurança necessárias.

A API foi construída seguindo as boas práticas de arquitetura, utilizando Spring Data JPA para persistência em banco de dados Oracle SQL e Spring Security com JWT para autenticação e autorização.

**Principais Funcionalidades da API:**

* **Gestão de Boletins:** Endpoints CRUD (Create, Read, Update, Delete) para os boletins de alerta e informação.
* **Autenticação de Utilizadores:** Endpoints para registo e login de utilizadores, com geração de tokens JWT para acesso seguro aos recursos protegidos.
* **Paginação e Ordenação:** Suporte para paginação e ordenação nos endpoints de listagem.
* **Filtros:** Capacidade de filtrar boletins por critérios como severidade ou título.
* **Validação:** Uso de Bean Validation para garantir a integridade dos dados recebidos.
* **Documentação:** Documentação interativa da API gerada com Swagger/OpenAPI.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17**
* **Spring Boot 3.2.x** (ou a versão que estiver a usar)
    * Spring Web (MVC)
    * Spring Data JPA
    * Spring Security
* **Maven** (para gestão de dependências e build)
* **Oracle SQL** (como banco de dados relacional)
* **JWT (JSON Web Tokens)** com a biblioteca `jjwt` para autenticação.
* **Lombok** (para reduzir código boilerplate)
* **SpringDoc OpenAPI / Swagger** (para documentação da API)
* **HikariCP** (para pooling de conexões com o banco de dados)

---

## 🔗 Links dos Repositórios

* **Repositório Frontend (React Native):** [LINK_REPOSITORIO_FRONTEND_GITHUB_CLASSROOM]

---

## 📋 Instruções para Execução Local e Testes da API

### Pré-requisitos para Execução Local:

1.  Java JDK 17 (ou superior compatível) instalado.
2.  Apache Maven instalado.
3.  Acesso a uma instância de banco de dados Oracle SQL.
4.  Configurar as credenciais do banco de dados no ficheiro `src/main/resources/application.properties`.

### Executando a Aplicação Localmente:

1.  Clone este repositório:
    ```bash
    git clone https://github.com/Gu1LhermeF5P/comunidade_java_v
    cd comunidade-java 
    ```
2.  Configure o ficheiro `src/main/resources/application.properties` com os seus dados de conexão Oracle:
    ```properties
    spring.datasource.url=jdbc:oracle:thin:@SEU_HOST_ORACLE:PORTA:SID_OU_SERVICE_NAME
    spring.datasource.username=SEU_USUARIO_ORACLE(usuario oracle no .txt da entrega)
    spring.datasource.password=SUA_SENHA_ORACLE
    jwt.secret=SEU_SEGREDO_JWT_FORTE_COM_PELO_MENOS_32_BYTES
    ```
3.  Compile e execute a aplicação usando Maven:
    ```bash
    mvn spring-boot:run
    ```
    Ou, se preferir construir o JAR primeiro:
    ```bash
    mvn clean package -DskipTests
    java -jar target/comunidade-java-0.0.1-SNAPSHOT.jar 
    ```
    A API estará disponível em `http://localhost:8080`.

### Testando os Endpoints (com Postman ou Insomnia)

**URL Base Local:** `http://localhost:8080`
Boletins**
   *(Para os endpoints POST, PUT, DELETE, adicione o header: `Authorization: Bearer SEU_ACCESS_TOKEN`)*

* **Criar Boletim:**
    * `POST /api/boletins`
    * Header: `Content-Type: application/json`, `Authorization: Bearer SEU_ACCESS_TOKEN`
    * Corpo (JSON):
        ```json
        {
          "title": "Alerta de Enchente no Bairro X",
          "location": "Rua Y, próximo ao rio Z",
          "content": "Nível do rio subindo rapidamente. Risco de enchente iminente.",
          "severity": "Alerta"
        }
        ```
    * Resposta Esperada: `201 Created` com os dados do boletim criado (incluindo `id` e `username` do criador).

* **Listar Todos os Boletins:**
    * `GET /api/boletins`
    * (Opcional) Query Params: `?page=0&size=5&sort=timestamp,desc&severity=Alerta&title=Enchente`
    * Resposta Esperada: `200 OK` com a página de boletins.

* **Buscar Boletim por ID:**
    * `GET /api/boletins/{id}` (substitua `{id}` por um ID válido)
    * Resposta Esperada: `200 OK` com os dados do boletim, ou `404 Not Found`.

* **Atualizar Boletim:**
    * `PUT /api/boletins/{id}`
    * Header: `Content-Type: application/json`, `Authorization: Bearer SEU_ACCESS_TOKEN`
    * Corpo (JSON):
        ```json
        {
          "title": "Alerta de Enchente ATUALIZADO",
          "location": "Rua Y, próximo ao rio Z - Nível Estabilizado",
          "content": "O nível do rio estabilizou, mas a área continua em observação.",
          "severity": "Cuidado"
        }
        ```
    * Resposta Esperada: `200 OK` com os dados atualizados, ou `404 Not Found`.

* **Excluir Boletim:**
    * `DELETE /api/boletins/{id}`
    * Header: `Authorization: Bearer SEU_ACCESS_TOKEN`
    * Resposta Esperada: `204 No Content` (se o utilizador tiver permissão, ex: `ROLE_ADMIN`), `403 Forbidden` (se não tiver permissão), ou `404 Not Found`.
