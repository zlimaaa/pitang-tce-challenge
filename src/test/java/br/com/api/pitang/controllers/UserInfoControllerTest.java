package br.com.api.pitang.controllers;


import br.com.api.pitang.configs.security.UserDetail;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@DisplayName("Teste de integracao info do usuario logado")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserInfoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserInfoController controller;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    @DisplayName("Preparando para iniciar o teste com O usuario salvo no banco e na sessao")
    public void setUp() {
        mockMvc = standaloneSetup(controller).build();

        User user = buildUsers().get(3);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(new UserDetail(user),null));
        setContext(securityContext);
    }

    @Test
    @DisplayName("Consultando usuario logado")
    public void findUser() throws Exception {

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marcos"))
                .andExpect(jsonPath("$.lastName").value("Pontes"))
                .andExpect(jsonPath("$.birthDate").value("30/10/1980"))
                .andExpect(jsonPath("$.email").value("marcos@gmail.com"))
                .andExpect(jsonPath("$.login").value("marcos"))
                .andExpect(jsonPath("$.phone").value("87987695672"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @AfterAll
    @DisplayName("Deletando o usario criado no banco de dados")
    public void deleteUserAfterFinishTests() {
        userRepository.findDistinctByLogin("marcos")
                .ifPresent(
                        u -> userRepository.deleteById(u.getId())
                );
    }
}
