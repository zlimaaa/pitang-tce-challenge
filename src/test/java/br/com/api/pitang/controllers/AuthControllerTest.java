package br.com.api.pitang.controllers;

import static br.com.api.pitang.constants.MessagesConstants.INVALID_LOGIN;
import br.com.api.pitang.data.dtos.AuthDTO;
import static br.com.api.pitang.data.dtos.AuthDTO.builder;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.UserRepository;
import br.com.api.pitang.services.UserService;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.web.util.NestedServletException;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Teste de integracao de autenticacao")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private AuthController controller;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeAll
    @DisplayName("Criando um usario no banco para ser usado no login")
    public void setUp() {
        User user = buildUsers().get(3);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        this.mockMvc = standaloneSetup(controller).build();
    }

    @Test
    @Order(1)
    @DisplayName("Erro ao criar autenticação, email/senha incorretos")
    public void errorCreateAuth() {
        AuthDTO dto = builder()
                .login("marcos")
                .password("01669988")
                .build();

        try {
            this.mockMvc.
                    perform(post("/api/signin")
                            .contentType(APPLICATION_JSON)
                            .content(gson.toJson(dto)))
                    .andExpect(status().isBadRequest());
        } catch (Exception ex) {
            assertEquals(NestedServletException.class, ex.getClass());
            assertTrue(ex.getMessage().contains(INVALID_LOGIN));
        }

    }

    @Test
    @Order(2)
    @DisplayName("Criando autenticação com sucesso e validando update do ultimo login")
    public void createAuthAndVerifyLastLogin() throws Exception {
        User user = this.userService.findByLogin("marcos");
        assertNull(user.getLastLogin());

        AuthDTO dto = builder()
                .login("marcos")
                .password("18855698")
                .build();

        this.mockMvc.
                perform(post("/api/signin")
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marcos"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        user = this.userService.findByLogin("marcos");
        assertNotNull(user.getLastLogin());
    }

    @Test
    @Order(3)
    @DisplayName("Criando autenticação com sucesso")
    public void createAuth() throws Exception {
        AuthDTO dto = builder()
                .login("marcos")
                .password("18855698")
                .build();

        this.mockMvc.
                perform(post("/api/signin")
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marcos"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @AfterAll
    @DisplayName("Deletando o usario criado no banco de dados")
    public void deleteUserAfterFinishTests(){
       this.userRepository.findDistinctByLogin("marcos")
               .ifPresent(
                       u -> this.userRepository.deleteById(u.getId())
               );
    }
}
