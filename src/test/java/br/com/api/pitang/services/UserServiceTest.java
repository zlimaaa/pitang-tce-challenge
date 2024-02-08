package br.com.api.pitang.services;

import static br.com.api.pitang.constants.MessagesConstants.EMAIL_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LOGIN_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.data.models.User;
import br.com.api.pitang.exceptions.ValidationException;
import static br.com.api.pitang.factory.UserFactory.buildUserDTOs;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.UserRepository;
import java.time.LocalDate;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes unitarios do usuario")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;

    @Test
    @Order(1)
    @DisplayName("Criando um usuario")
    public void createUser() {
        when(this.repository.countByEmailAndIdNot("ricardo@gmail.com", 0L)).thenReturn(0L);
        when(this.repository.countByLoginAndIdNot("ricardo", 0L)).thenReturn(0L);
        when(this.repository.save(any(User.class))).thenReturn(buildUsers().get(0));

        UserDTO userDTO = buildUserDTOs().get(0);
        userDTO.setId(null);
        userDTO.setLogin("RicarDo");

        userDTO = this.service.save(userDTO);

        assertEquals(1L, userDTO.getId());
        assertEquals("Ricardo", userDTO.getFirstName());
        assertEquals("Lima", userDTO.getLastName());
        assertEquals(LocalDate.of(1997,12,14), userDTO.getBirthDate());
        assertEquals("ricardo@gmail.com", userDTO.getEmail());
        assertEquals("ricardo", userDTO.getLogin());
        assertEquals("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", userDTO.getPassword());
        assertEquals("81988775423", userDTO.getPhone());
        assertNotNull(userDTO.getCreatedAt());
        assertNull(userDTO.getLastLogin());
    }

    @Test
    @Order(2)
    @DisplayName("Erro ao criar usuario com email ja existente")
    public void errorCreatingUserT01() {
        when(repository.countByEmailAndIdNot("luiza@gmail.com", 0L)).thenReturn(1L);

        try {
            service.save(buildUserDTOs().get(2));
        }catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(EMAIL_ALREADY_EXISTS, ex.getMessage());
        }
    }
    @Test
    @Order(3)
    @DisplayName("Erro ao criar usuario com login ja existente")
    public void errorCreatingUserT02() {
        when(repository.countByLoginAndIdNot("luzinha", 0L)).thenReturn(1L);

        try {
            service.save(buildUserDTOs().get(2));
        }catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(LOGIN_ALREADY_EXISTS, ex.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Erro ao criar usuario sem informar o primeiro nome")
    public void errorCreatingUserT03() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setFirstName("");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }
    @Test
    @Order(5)
    @DisplayName("Erro ao criar usuario sem informar o ultimo nome")
    public void errorCreatingUserT04() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setLastName("");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Erro ao criar usuario sem informar data de nascimento")
    public void errorCreatingUserT05() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setBirthDate(null);

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Erro ao criar usuario data de nascimento invalida")
    public void errorCreatingUserT06() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setBirthDate(LocalDate.now().plusMonths(2L));

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Erro ao criar usuario informando email invalido")
    public void errorCreatingUserT07() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setEmail("luiza@");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Erro ao criar usuario sem informar email")
    public void errorCreatingUserT08() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setEmail(null);

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Erro ao criar usuario telefone invalido")
    public void errorCreatingUserT09() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setPhone("8187661302");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }
    @Test
    @Order(11)
    @DisplayName("Erro ao criar usuario sem informar telefone")
    public void errorCreatingUserT10() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setPhone("");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Erro ao criar usuario sem informar login")
    public void errorCreatingUserT11() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setLogin(null);

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Erro ao criar usuario sem informar senha")
    public void errorCreatingUserT12() {
        UserDTO userDTO = buildUserDTOs().get(2);
        userDTO.setPassword("     ");

        try {
            service.save(userDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Consultando usuario por id")
    public void findUserById() {
        when(this.repository.findDistinctById(2L)).thenReturn(of(buildUsers().get(1)));

        UserDTO userDTO = service.findOne(2L);

        assertEquals(2L, userDTO.getId());
        assertEquals("Fernando", userDTO.getFirstName());
        assertEquals("Costa", userDTO.getLastName());
        assertEquals(LocalDate.of(1999,2,1), userDTO.getBirthDate());
        assertEquals("nando@gmail.com", userDTO.getEmail());
        assertEquals("nando01", userDTO.getLogin());
        assertEquals("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC", userDTO.getPassword());
        assertEquals("11989774271", userDTO.getPhone());
        assertNotNull(userDTO.getCreatedAt());
        assertNotNull(userDTO.getLastLogin());
    }

    @Test
    @Order(15)
    @DisplayName("Consultando usuario por login")
    public void findUserByLogin() {
        when(this.repository.findDistinctByLogin("nando01")).thenReturn(of(buildUsers().get(1)));

        User user = service.findByLogin("nando01");

        assertEquals(2L, user.getId());
        assertEquals("Fernando", user.getFirstName());
        assertEquals("Costa", user.getLastName());
        assertEquals(LocalDate.of(1999,2,1), user.getBirthDate());
        assertEquals("nando@gmail.com", user.getEmail());
        assertEquals("nando01", user.getLogin());
        assertEquals("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC", user.getPassword());
        assertEquals("11989774271", user.getPhone());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getLastLogin());
    }
}
