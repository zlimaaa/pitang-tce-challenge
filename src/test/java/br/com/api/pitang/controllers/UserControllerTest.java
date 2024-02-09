package br.com.api.pitang.controllers;

import br.com.api.pitang.adapters.LocalDateTimeTypeAdapter;
import br.com.api.pitang.adapters.LocalDateTypeAdapter;
import br.com.api.pitang.configs.security.UserDetail;
import static br.com.api.pitang.constants.MessagesConstants.EMAIL_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LOGIN_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.USER_NOT_FOUND;
import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUserDTOs;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.UserRepository;
import static br.com.api.pitang.utils.DozerConverter.convertObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes de integracao do usuario")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserController controller;

    @Autowired
    private UserRepository repository;

    private Long userId;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();

    @BeforeAll
    @DisplayName("Preparando para iniciar os testes com um usuario salvo no banco e na sessao")
    public void setUp() {
        mockMvc = standaloneSetup(controller).build();
        User user = buildUsers().get(2);
        user.setCreatedAt(LocalDateTime.now());
        user = repository.save(user);
        userId = user.getId();

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(new UserDetail(user),null));
        setContext(securityContext);
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("Criando usuario com sucesso")
    public void createUser() throws Exception {
        UserDTO userDTO = buildUserDTOs().get(0);
        userDTO.setId(null);

        mockMvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Ricardo"))
                .andExpect(jsonPath("$.lastName").value("Lima"))
                .andExpect(jsonPath("$.birthDate").value("14/12/1997"))
                .andExpect(jsonPath("$.email").value("ricardo@gmail.com"))
                .andExpect(jsonPath("$.login").value("ricardo"))
                .andExpect(jsonPath("$.phone").value("81988775423"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Erro ao criar usuario com email existente")
    public void errorCreateUserWithEmailAlreadyExists() throws Exception {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setEmail("luquinhas@gmail.com");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertEquals(EMAIL_ALREADY_EXISTS, errorMessage);
    }

    @Test
    @Order(3)
    @DisplayName("Erro ao criar usuario com email invalido")
    public void errorCreateUserWithInvalidEmail() throws Exception {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setEmail("@lui.za");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

       assertTrue(errorMessage.contains(INVALID_FIELDS));
    }

    @Test
    @Order(4)
    @DisplayName("Erro ao criar usuario com login existente")
    public void errorCreateUserWithLoginAlreadyExists() throws Exception {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setLogin("luquinhas");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertEquals(LOGIN_ALREADY_EXISTS, errorMessage);
    }

    @Test
    @Order(5)
    @DisplayName("Erro ao criar usuario com campos faltando")
    public void errorCreateUserWithMissingFields() throws Exception {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setLogin(" ");
        userDTO.setPhone(null);

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(MISSING_FIELDS));
    }

    @Test
    @Order(6)
    @DisplayName("Consultando usuario pelo id")
    public void findUser() throws Exception {
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Lucas"))
                .andExpect(jsonPath("$.lastName").value("Filho"))
                .andExpect(jsonPath("$.birthDate").value("11/11/2000"))
                .andExpect(jsonPath("$.email").value("luquinhas@gmail.com"))
                .andExpect(jsonPath("$.login").value("luquinhas"))
                .andExpect(jsonPath("$.phone").value("21988826756"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Erro ao consultar usuario com id inexistente")
    public void errorFindUser() {

        try {
            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound());
        } catch (Exception ex) {
            assertEquals(NestedServletException.class, ex.getClass());
            assertTrue(ex.getMessage().contains(USER_NOT_FOUND));
        }
    }

    @Test
    @Order(8)
    @DisplayName("Consultando todos os usuarios")
    public void findAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value(userId))
                .andExpect(jsonPath("$.content.[0].firstName").value("Lucas"))
                .andExpect(jsonPath("$.content.[0].lastName").value("Filho"))
                .andExpect(jsonPath("$.content.[0].birthDate").value("11/11/2000"))
                .andExpect(jsonPath("$.content.[0].email").value("luquinhas@gmail.com"))
                .andExpect(jsonPath("$.content.[0].login").value("luquinhas"))
                .andExpect(jsonPath("$.content.[0].phone").value("21988826756"))
                .andExpect(jsonPath("$.content.[0].createdAt").isNotEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("Atualizando usuario com sucesso")
    public void updateUser() throws Exception {
        User user = buildUsers().get(2);
        user.setBirthDate(LocalDate.of(1998, 4, 25));
        user.setLastName("Santos");
        UserDTO userDTO = convertObject(user, UserDTO.class);
        mockMvc.perform(put("/api/users/" + userId).contentType(APPLICATION_JSON).content(gson.toJson(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Lucas"))
                .andExpect(jsonPath("$.lastName").value("Santos"))
                .andExpect(jsonPath("$.birthDate").value("25/04/1998"))
                .andExpect(jsonPath("$.email").value("luquinhas@gmail.com"))
                .andExpect(jsonPath("$.login").value("luquinhas"))
                .andExpect(jsonPath("$.phone").value("21988826756"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("Exluindo usuario com sucesso")
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + userId)).andExpect(status().isNoContent());

        assertTrue(repository.findById(userId).isEmpty());
    }
    @Test
    @Order(11)
    @DisplayName("Erro ao excluir usuario inexistente")
    public void deleteUserError() throws Exception {
        try {
            mockMvc.perform(delete("/api/users/99")).andExpect(status().isNotFound());
        } catch (Exception ex) {
            assertEquals(NestedServletException.class, ex.getClass());
            assertTrue(ex.getMessage().contains(USER_NOT_FOUND));
        }
    }


}
