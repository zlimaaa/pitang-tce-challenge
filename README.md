# Desafio Pitang TCE - API 

Api desenvolvida em Java 11 e com Spring Boot 2.6.7

## Iniciando a Api sem Docker

#### Branch Master: (com banco de dados PostegreSQL)

1. Importar o projeto no eclipse ou intelli j;
2. Configurar o arquivo `src/main/resources/application.properties` com as informações de conexão de um banco de dados `PostgreSQL`;
3. Executar o comando no terminal `mvn clean install` ou `mvn clean install -DskipTests` (para pular os testes);
4. Executar a classe `StartApplication.java` ou executar o comando no terminal  `mvn spring-boot:run`;

#### Branch Developer: (com banco de dados H2 em memória)
1. Importar o projeto no eclipse ou intelli j;
2. Executar o comando no terminal `mvn clean install`;
3. Executar a classe `StartApplication.java` ou executar o comando no terminal  `mvn spring-boot:run`;

## Iniciando a Api com Docker

1. Entrar na raiz do projeto, onde está o arquivo `docker-compose.yml`;
2. Rodar o comando `docker-compose up -d --build` e esperar subir os containers;

## Solução 

De acordo com o desafio proposto foi construida uma API Restful simples e que atende os requisitos solicitado em sua grande 
maioria, o foco na construção dessa API foi manter uma simplicidade, mantenabilidade e qualidade. Para isso construir uma API 
que espera ter uma clareza do código, à modularidade, à facilidade de compreensão e manutenção do sistema como um todo. Tanto
o backend quanto o frontend busquei trazer qualidade, clareza e reutilização do código. Por se tratar de uma API simples e pequena
optei por não trazer maiores complexidades e verbosidades, nem maiores recursos e abstrações.  

### Tecnologias, ferramentas e frameworks utilizados na construção da API:
 
- Java 11;
- Spring Framework
  - Spring Boot;
  - Spring Boot Web;
  - Spring Boot Security;
  - Spring Boot Data JPA;
  - Spring Boot Test;
  - Spring Boot Validations;
  - Spring Boot DevTools;
- PostgreSQL;
- H2 Database;   
- Hibernate;   
- Lombok;
- Swagger 2;
- Jason Web Token (JWT);
- Maven;
- Gson;
- Dozer Mapper;
- Docker e Docker Compose;

### Funcionalidades e Requisitos entregues:
<br>

#### Requisitos:
- -[x] JWT como token    
- -[x] Servidor deve estar embutido na aplicação (Tomcat, Undertow ou Jetty);
- -[x] Processo de build via Maven;
- -[x] Framework Spring;
- -[x] Utilizar no mínimo Java 8;
- -[x] Persistência com JPA/Hibernate;
- -[x] Disponibilizar a API rodando em algum host (Heroku, AWS, Digital Ocean, etc);
- -[x] Criar um repositório público em alguma ferramenta de git (Github, Gitlab, Bitbucket, etc)
- -[x] README.md 
- -[x] Criar um front-end mínimo com tecnologia baseada em JS (preferencialmente em Angular);
- -[x] A aplicação deve aceitar e responder apenas em JSON;
- -[x] Todas as rotas devem responder o código de status HTTP apropriado;
- -[x] Espera-se que as mensagens de erro tenham o seguinte formato: 
{ "message": "Error message", "errorCode": 123 }
 
 <br>   

#### Requisitos Desejáveis: 
    
- -[x] Agendamento de tarefas (Ex: @Scheduled do Spring);
- -[x] Senha deve ser criptografada;
- -[x] Documentação (Javadoc);
- -[x] Design Pattern 
- -[x] Swagger;
                   
 <br>                       
                                  
#### Requisito extra (bonus stage): 

- -[x] Construir uma estrutura de dados ordenada para listar os carros;
- -[x] Construir uma estrutura de dados ordenada para listar os usuários;

             
## Estorias de Usuário

Por se tratar de um projeto pequeno e simples eu dividir o desenvolvimento da API em  6 etapas, pensando na metodologia 
do Scrum onde o foco é realizar pequenas entregas, porém funcionais...

1. Criação e configuração da estrutura inicial da API; (feature/001)
2. Desenvolvimento dos endpoints, regras, testes e validações do usuário [CRUD]; (feature/002)
3. Desenvolvimento das endpoints, regras, testes e validações do carro [CRUD]; (feature/003)
4. Desenvolvimento de melhorias, ajustes e requisitos desejaveis e o bonus; (feature/004)
5. Desenvolvimento do README.md e possiveis ajustes; (feature/005)
6. Desenvolvimento do frontend em um repositório a parte;
                                                             
### CRUD - Usuários
 ```      
1. Cadastrar um Novo Usuário (/api/users):
    - Como usuário do sistema, desejo poder cadastrar um novo usuário fornecendo informações como nome, sobrenome, 
   e-mail, data de nascimento, login, senha e telefone, para permitir o acesso total ao sistema.
     
                                     
     - Critérios de Aceitação: 
       - A API deve aceitar uma solicitação POST com os dados do novo usuário.
       - Se o cadastro for bem-sucedido, a API deve retornar o usuário recém-criado com o status 201 Created.
       - Se ocorrerem erros de validação, a API deve retornar uma mensagem de erro com o status apropriado.
             
     - Cenários de erros:
        1. E-mail já existente: retornar um erro com a mensagem “Email already exists”;
        2. Login já existente: retornar um erro com a mensagem “Login already exists”;
        3. Campos inválidos: retornar um erro com a mensagem “Invalid fields”;  
        4. Campos não preenchidos: retornar um erro com a mensagem “Missing fields”.


2. Listar Todos os Usuários (/api/users):

    - Como usuário do sistema, desejo poder visualizar todos os usuários cadastrados, 
    para ter uma visão geral dos usuários registrados.
   
   
    - Critérios de Aceitação:
         - A API deve retornar uma lista de todos os usuários cadastrados no sistema.


3. Buscar um Usuário pelo ID (/api/users/{id}):
    
   - Como usuário do sistema, desejo poder buscar um usuário específico 
   pelo seu ID, para visualizar os detalhes desse usuário.
   

     - Critérios de Aceitação:                                                   
       - A API deve aceitar uma solicitação GET com o ID do usuário desejado.
       - Se o usuário for encontrado, a API deve retornar os detalhes desse usuário com o status 200 OK.
       - Se o usuário não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.

         
 4. Remover um Usuário pelo ID (/api/users/{id}):

    - Como usuário do sistema, desejo poder remover um usuário específico pelo seu ID, caso necessário.
                
    
    - Critérios de Aceitação: 
        - A API deve aceitar uma solicitação DELETE com o ID do usuário a ser removido.
        - Se o usuário for removido com sucesso, a API não precisará retornar um body e o status deverá ser 204 No Content.
        - Se o usuário não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.
        


 5. Atualizar um Usuário pelo ID (/api/users/{id}):
    
    - Como usuário do sistema, desejo poder atualizar as informações de um usuário específico 
      pelo seu ID, caso necessário.
         
         
         - Critérios de Aceitação:
             - A API deve aceitar uma solicitação PUT com o ID do usuário a ser atualizado juntamente com as informaçõs do 
                usuário como nome, sobrenome, e-mail, data de nascimento, login e telefone todos esses campos deverão ser informados, 
                mesmo que não forem alterados, no caso do campo senha, o mesmo só deverá ser informado caso haja a intenção de atualizar a senha.
             - Se a atualização for bem-sucedida, a API deve retornar o usuário atualizado com o status 200 OK.
             - Se o usuário não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.
             - Se ocorrerem erros de validação, a API deve retornar uma mensagem de erro com o status apropriado.
                
          - Cenários de erros:                                                                
               1. E-mail já existente: retornar um erro com a mensagem “Email already exists”;
               2. Login já existente: retornar um erro com a mensagem “Login already exists”; 
               3. Campos inválidos: retornar um erro com a mensagem “Invalid fields”;          
               4. Campos não preenchidos: retornar um erro com a mensagem “Missing fields”.        

```

<br>

### Login
```
1. Fazer login no sistema (/api/signin):
    - Como usuário do sistema, desejo poder me autenticar no sistema, para que seja possivel o acesso total ao sistema.


    - Critérios de Aceitação:
        - A API deve aceitar uma solicitação POST com os dados de login e senha do usuário.
        - Se o login for bem-sucedido, a API deve retornar ao usuário as seguintes informações:
           nome do usuário e um token válido e o status 200 OK.
        - Se ocorrerem erros, a API deve retornar uma mensagem de erro com o status apropriado.

    - Cenários de erros:
        1. Login inexistente ou senha inválida: retornar um erro com a mensagem “Invalid login or password”;

```

<br>

### CRUD - Carros
```
1. Cadastrar um Novo Carro (/api/cars):
    - Como um usuário autenticado no sistema, desejo poder cadastrar um novo carro fornecendo informações como ano, modelo,
      cor e placa do carro, para registrar-lo no sistema.


    - Critérios de Aceitação:
        - A API deve aceitar uma solicitação POST com os dados do novo carro.
        - Se o cadastro for bem-sucedido, a API deve retornar o carro recém-criado com o status 201 Created.
        - Se ocorrerem erros de validação, a API deve retornar uma mensagem de erro com o status apropriado.

    - Cenários de erros:
      1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
      2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;
      3. Placa já existente: retornar um erro com a mensagem “License plate already exists”;
      4. Campos inválidos: retornar um erro com a mensagem “Invalid fields”;
      5. Campos não preenchidos: retornar um erro com a mensagem “Missing fields”.


2. Listar Todos os Carros que pertence ao usuário autenticado no sistema (/api/cars):

    - Como um usuário autenticado no sistema, desejo poder visualizar todos os carros que estão cadastrado em meu nome,
   para ter uma visão geral dos carros registrados.


    - Critérios de Aceitação:
      - A API deve retornar uma lista de todos os carros cadastrados em meu nome no sistema.
      - Se ocorrerem erros, a API deve retornar uma mensagem de erro com o status apropriado.

    - Cenários de erros:
      1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
      2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;


3. Buscar um Carro pelo ID (/api/cars/{id}):

    - Como um usuário autenticado no sistema, desejo poder buscar um carro específico
     pelo seu ID, para visualizar os detalhes desse carro.

    - Critérios de Aceitação:
        - A API deve aceitar uma solicitação GET com o ID do carro desejado.
        - Se o carro for encontrado, a API deve retornar os detalhes desse carro com o status 200 OK.
        - Se o carro não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.
        - Se ocorrerem erros, a API deve retornar uma mensagem de erro com o status apropriado.

    - Cenários de erros:
        1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
        2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;
        3. Permissão negada: retornar um erro com a mensagem “Unauthorized - permission denied”;


4. Remover um Carro pelo ID (/api/cars/{id}):

    - Como um usuário autenticado no sistema, desejo poder remover um carro específico 
    pelo seu ID, caso necessário.

    - Critérios de Aceitação:
        - A API deve aceitar uma solicitação DELETE com o ID do carro a ser removido.
        - Se o carro for removido com sucesso, a API não precisará retornar um body e o status deverá ser 204 No Content.
        - Se o carro não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.
        - Se ocorrerem erros, a API deve retornar uma mensagem de erro com o status apropriado.

    - Cenários de erros:
        1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
        2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;
        3. Permissão negada: retornar um erro com a mensagem “Unauthorized - permission denied”;


5. Atualizar um Carro pelo ID (/api/cars/{id}):

    - Como um usuário autenticado no sistema, desejo poder atualizar as informações de um carro 
    específico pelo seu ID, caso necessário.

       - Critérios de Aceitação:
           - A API deve aceitar uma solicitação PUT com o ID do carro a ser atualizado juntamente com as informaçõs do
              carro como ano, modelo, cor e placa do carro todos esses campos deverão ser informados,
              mesmo que não forem alterados.
           - Se a atualização for bem-sucedida, a API deve retornar o carro atualizado com o status 200 OK.
           - Se o carro não for encontrado, a API deve retornar uma mensagem de erro com o status 404 Not Found.
           - Se ocorrerem erros de validação, a API deve retornar uma mensagem de erro com o status apropriado.

      - Cenários de erros:
          1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
          2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;
          3. Permissão negada: retornar um erro com a mensagem “Unauthorized - permission denied”;
          4. Placa já existente: retornar um erro com a mensagem “License plate already exists”;
          5. Campos inválidos: retornar um erro com a mensagem “Invalid fields”;
          6. Campos não preenchidos: retornar um erro com a mensagem “Missing fields”.

```
<br>

### User Info
```
1. Buscar informações do usuário logado (/api/me}):

    - Como um usuário autenticado no sistema, desejo poder consultar as informações do meu usuario no sistema, 
    para visualizar os detalhes adicionais.


        - Critérios de Aceitação:
            - A API deve aceitar uma solicitação GET com um Authorization valido.
            - A API deve retornar os detalhes desse usuário, acrescentado dos campos createdAt 
                (data da criação do usuário) e lastLogin (data da última vez que o usuário realizou login)
                com o status 200 OK.
            - Se ocorrerem erros, a API deve retornar uma mensagem de erro com o status apropriado.

        - Cenários de erros:
            1. Token não enviado: retornar um erro com a mensagem “Unauthorized”;
            2. Token expirado: retornar um erro com a mensagem “Unauthorized - invalid session”;
```

<br>

## Usando a Api (Primeiros passos)


`base url local: http://localhost:8080` </br>
`base url heroku: https://dextra-challenge.herokuapp.com`

1. Via postman:
    Exemplos de Requests:


    CADASTRAR USUÁRIO:
```
url: http://localhost:8080/api/users  
metodo: POST 
body (Json):  

  {
    "birthDate": "27/02/1997",
    "email": "ricardo@gmail.com",
    "firstName": "ricardo",
    "lastName": "lima",
    "login": "ricardo",
    "password": "123123",
    "phone": "81989987667"	
  }

Exemplo de Response: 

 {
    "id": 88,
    "firstName": "ricardo",
    "lastName": "lima",
    "birthDate": "27/02/1997",
    "email": "ricardo@gmail.com",
    "login": "ricardo",
    "phone": "81989987667",
    "createdAt": "15/02/2024 17:40:43.559"
 }      
```

    REALIZAR LOGIN: (Necessário para utilizar as funcionalidades dos Carros) 

```  
url: http://localhost:8080/api/signin  
metodo: POST 
body (Json):

  {
    "login": "ricardo",
    "password": "123123"
  }

  Exemplo de Response: 

  {
	"name": "ricardo",
	"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiR68OH7feU9ccxwHwYoA..."
  }
```

    CADASTRAR CARRO:

```
    url:http://localhost:8080/api/cars 
    metodo: POST 
    header: 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiR68OH7feU9ccxwHwYoA...'
    body (Json): 
    
      {
        "year": 2023,
        "licensePlate": "POM-1A02",
        "model": "Renegade",
        "color": "Preto"
      }

    Exemplo de Response:

      {
        "id": 23,
        "year": 2023,
        "licensePlate": "POM-1A02",
        "model": "Renegade",
        "color": "Preto"
      }
```
    CONSULTAR CARRO:

```
    url:http://localhost:8080/api/cars/23 
    metodo: GET 
    header: 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiR68OH7feU9ccxwHwYoA...'
    
    Exemplo de Response:

      {
        "id": 23,
        "year": 2023,
        "licensePlate": "POM-1A02",
        "model": "Renegade",
        "color": "Preto"
      }
```
    
Esses são alguns exemplos dos endpoints, para saber mais sobre os demais endpoints acesse o swagger. 

2. Via Swegger:

    url: http://localhost:8080/swagger-ui.html

## Testando a Api

### Formas de testar a API
1. Executando todos os testes (Unitários, Repositorios, Integrados) com o comando `mvn test` ou o comando informado no começo.
2. Executando as classes de testes ou metodos individualmente com o JUnit Test;

### Cobertura de testes da API

![img.png](img.png)


## Links da API
HEROKU: https://pitang-tce-api-51bcd86fe773.herokuapp.com/swagger-ui.html </br>
GITHUB: https://github.com/zlimaaa/pitang-tce-challenge </br>

## Links do Frontend
HEROKU: https://pitang-tce-web-29e64535a365.herokuapp.com/</br>
GITHUB: https://github.com/zlimaaa/pitang-tce-challenge-web </br>
ENCURTADOR: https://bit.ly/pitang-tce


## Contatos

[LinkedIn](https://www.linkedin.com/in/zlimaa)
[GitHub](https://github.com/zlimaaa)
[Email](ricardolima.dev@gmail.com)
