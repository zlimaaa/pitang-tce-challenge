package br.com.api.pitang.repositories;


import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
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
        User userAux = buildUsers().get(0);
        userAux.setTotalUsageCounter(22L);
        userAux.setCreatedAt(LocalDateTime.now().minusWeeks(5));
        user = repository.save(userAux);
        repository.save(buildUsers().get(1));
    }

    @Test
    @Order(1)
    @DisplayName("Criando usuario")
    public void createUser() {
       User user = repository.save(buildUsers().get(2));

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
        User user = repository.findDistinctById(this.user.getId()).get();

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
        Long countUsers = repository.countByEmailAndIdNot(user.getEmail(), user.getId());

        assertEquals(0L, countUsers);

        countUsers = repository.countByEmailAndIdNot("nando@gmail.com", user.getId());

        assertEquals(1L, countUsers);

    }
    @Test
    @Order(4)
    @DisplayName("Contando usuarios pelo login")
    public void countUsersByLoginAndIdNot() {
        Long countUsers = repository.countByLoginAndIdNot(user.getLogin(), user.getId());

        assertEquals(0L, countUsers);

        countUsers = repository.countByLoginAndIdNot("nando01", user.getId());

        assertEquals(1L, countUsers);
    }
    @Test
    @Order(5)
    @DisplayName("Consultando usuario pelo login")
    public void findUserByLogin() {
        User user = repository.findDistinctByLogin(this.user.getLogin()).get();

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

        assertNull(user.getLastLogin());

        repository.updateLastLogin(user.getId(), LocalDateTime.now());

        User user = repository.findDistinctById(this.user.getId()).get();

        assertNotNull(user.getLastLogin());
    }

    @Test
    @Order(8)
    @DisplayName("Listando todos os usuarios com a regra do bonus stage")
    public void findAllWithOrderBy() {

        User userTwo = buildUsers().get(4);
        userTwo.setId(null);
        userTwo.setCars(null);
        userTwo.setTotalUsageCounter(15L);
        repository.save(userTwo);

        Sort sort = by(desc("totalUsageCounter"), asc("login"));
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<User> users = repository.findAll(pageable);

        assertEquals(3, users.getTotalElements());
        assertEquals("Ricardo", users.getContent().get(0).getFirstName());
        assertEquals("Lima", users.getContent().get(0).getLastName());
        assertEquals(LocalDate.of(1997,12,14), users.getContent().get(0).getBirthDate());
        assertEquals("ricardo@gmail.com", users.getContent().get(0).getEmail());
        assertEquals("ricardo", users.getContent().get(0).getLogin());
        assertEquals("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", users.getContent().get(0).getPassword());
        assertEquals("81988775423", users.getContent().get(0).getPhone());

        assertEquals("Otavio", users.getContent().get(1).getFirstName());
        assertEquals("Mendes", users.getContent().get(1).getLastName());
        assertEquals(LocalDate.of(1979,2,1), users.getContent().get(1).getBirthDate());
        assertEquals("otavio-m@gmail.com", users.getContent().get(1).getEmail());
        assertEquals("mendes", users.getContent().get(1).getLogin());
        assertEquals("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC", users.getContent().get(1).getPassword());
        assertEquals("87983772270", users.getContent().get(1).getPhone());

        assertEquals("Fernando", users.getContent().get(2).getFirstName());
        assertEquals("Costa", users.getContent().get(2).getLastName());
        assertEquals(LocalDate.of(1999,2,1), users.getContent().get(2).getBirthDate());
        assertEquals("nando@gmail.com", users.getContent().get(2).getEmail());
        assertEquals("nando01", users.getContent().get(2).getLogin());
        assertEquals("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC", users.getContent().get(2).getPassword());
        assertEquals("11989774271", users.getContent().get(2).getPhone());
    }


    @Test
    @Order(9)
    @DisplayName("Deletando usuario")
    public void deleteUserById() {
        repository.deleteById(user.getId());

        try {
            repository.findById(user.getId());
        }catch (Exception ex) {
            assertEquals(NoSuchElementException.class, ex.getClass());
            assertEquals("No value present", ex.getMessage());
        }
    }


    @Test
    @Order(10)
    @DisplayName("Deletando usuarios inativos")
    public void deleteInactiveUsers() {

        Long countUsers = repository.count();
        assertEquals(2, countUsers);

        User userOne = buildUsers().get(2);
        userOne.setCreatedAt(LocalDateTime.now().minusDays(31));

        User userTwo = buildUsers().get(3);
        userTwo.setCreatedAt(LocalDateTime.now().minusDays(31));
        userTwo.setLastLogin(LocalDateTime.now().minusDays(30));

        repository.save(userOne);
        repository.save(userTwo);

        countUsers = repository.count();
        assertEquals(4, countUsers);

        repository.deleteInactiveUsers(LocalDateTime.now().minusDays(30));

        countUsers = repository.count();
        assertEquals(1, countUsers);
    }
}
