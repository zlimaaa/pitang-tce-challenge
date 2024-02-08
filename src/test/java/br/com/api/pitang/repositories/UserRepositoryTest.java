package br.com.api.pitang.repositories;


import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes unitarios de persistencia do usuario")
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;
    private User user;

    @BeforeAll
    @DisplayName("Preparando para iniciar os testes com usuarios salvos no banco")
    public void setUp() {
        this.user = repository.save(buildUsers().get(0));
        repository.save(buildUsers().get(1));
    }

    @Test
    @Order(1)
    @DisplayName("Criando usuario")
    public void createUser() {
       User user = this.repository.save(buildUsers().get(2));

        assertNotNull(user.getId());
        assertEquals("Lucas", user.getFirstName());
        assertEquals("Filho", user.getLastName());
        assertEquals(LocalDate.of(2000,11,11), user.getBirthDate());
        assertEquals("luquinhas@gmail.com", user.getEmail());
        assertEquals("luquinhas", user.getLogin());
        assertEquals("$2a$10$.AfRZpLPQKTq.rRdQjtLNenLvrLKZrllYnZUZZOqlqsZsQ8zVCzde", user.getPassword());
        assertEquals("21988826756", user.getPhone());
    }

    @Test
    @Order(2)
    @DisplayName("Consultando usuario pelo id")
    public void findUserById() {
        User user = this.repository.findDistinctById(this.user.getId()).get();

        assertEquals(1L, user.getId());
        assertEquals("Ricardo", user.getFirstName());
        assertEquals("Lima", user.getLastName());
        assertEquals(LocalDate.of(1997,12,14), user.getBirthDate());
        assertEquals("ricardo@gmail.com", user.getEmail());
        assertEquals("ricardo", user.getLogin());
        assertEquals("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", user.getPassword());
        assertEquals("81988775423", user.getPhone());
    }

    @Test
    @Order(3)
    @DisplayName("Contando usuarios pelo email")
    public void countUsersByEmailAndIdNot() {
        Long countUsers = this.repository.countByEmailAndIdNot(this.user.getEmail(), this.user.getId());

        assertEquals(0L, countUsers);

        countUsers = this.repository.countByEmailAndIdNot("nando@gmail.com", this.user.getId());

        assertEquals(1L, countUsers);

    }
    @Test
    @Order(4)
    @DisplayName("Contando usuarios pelo login")
    public void countUsersByLoginAndIdNot() {
        Long countUsers = this.repository.countByLoginAndIdNot(this.user.getLogin(), this.user.getId());

        assertEquals(0L, countUsers);

        countUsers = this.repository.countByLoginAndIdNot("nando01", this.user.getId());

        assertEquals(1L, countUsers);
    }
    @Test
    @Order(5)
    @DisplayName("Consultando usuario pelo login")
    public void findUserByLogin() {
        User user = this.repository.findDistinctByLogin(this.user.getLogin()).get();

        assertEquals(1L, user.getId());
        assertEquals("Ricardo", user.getFirstName());
        assertEquals("Lima", user.getLastName());
        assertEquals(LocalDate.of(1997,12,14), user.getBirthDate());
        assertEquals("ricardo@gmail.com", user.getEmail());
        assertEquals("ricardo", user.getLogin());
        assertEquals("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", user.getPassword());
        assertEquals("81988775423", user.getPhone());
    }

    @Test
    @Order(6)
    @DisplayName("Atualizando ultimo login do usuario")
    public void updateLastLogin() {

        assertNull(this.user.getLastLogin());

        this.repository.updateLastLogin(user.getId(), LocalDateTime.now());

        User user = this.repository.findDistinctById(this.user.getId()).get();

        assertNotNull(user.getLastLogin());
    }
}
