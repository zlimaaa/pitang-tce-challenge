package br.com.api.pitang.utils;

import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUserDTOs;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import static br.com.api.pitang.utils.DozerConverter.convertObject;
import static br.com.api.pitang.utils.DozerConverter.convertObjects;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes unitarios do dozer converter")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DozerConverterTest {

    private final User user = buildUsers().get(0);
    private final UserDTO userDTO = buildUserDTOs().get(1);
    private final List<User> users = buildUsers();
    private final List<UserDTO> userDTOs = buildUserDTOs();

    @Test
    @DisplayName("Convertendo um usuário DTO para um usuário ")
    public void converterUserDTOtoUser() {
        User user = convertObject(userDTO, User.class);

        assertEquals(3L, user.getId());
        assertEquals("Ana", user.getFirstName());
        assertEquals("Maria", user.getLastName());
        assertEquals(LocalDate.of(1987,12,14), user.getBirthDate());
        assertEquals("ana", user.getLogin());
        assertEquals("aninha@gmail.com", user.getEmail());
        assertEquals("987654321", user.getPassword());
        assertEquals("81986234554", user.getPhone());
        assertNull(user.getCreatedAt());
        assertNull(user.getLastLogin());
    }

    @Test
    @DisplayName("Convertendo um usuário para um usuário DTO")
    public void converterUserToUserDTO() {
        UserDTO userDTO = convertObject(user, UserDTO.class);

        assertEquals(1L, userDTO.getId());
        assertEquals("Ricardo", userDTO.getFirstName());
        assertEquals("Lima", userDTO.getLastName());
        assertEquals(LocalDate.of(1997,12,14), userDTO.getBirthDate());
        assertEquals("ricardo@gmail.com", userDTO.getEmail());
        assertEquals("ricardo", userDTO.getLogin());
        assertEquals("81988775423", userDTO.getPhone());
        assertEquals("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", userDTO.getPassword());
        assertNotNull(userDTO.getCreatedAt());
        assertNull(userDTO.getLastLogin());
    }

    @Test
    @DisplayName("Convertendo uma lista de usuários para uma lista de usuários DTOs")
    public void converterUsersToUserDTOs() {
        List<UserDTO> userDTOs = convertObjects(users, UserDTO.class);

        assertEquals(4, userDTOs.size());
        assertEquals( 1L, userDTOs.get(0).getId());
        assertEquals( "Ricardo", userDTOs.get(0).getFirstName());
        assertEquals( "Lima", userDTOs.get(0).getLastName());
        assertEquals( LocalDate.of(1997,12,14), userDTOs.get(0).getBirthDate());
        assertEquals( "ricardo@gmail.com", userDTOs.get(0).getEmail());
        assertEquals( "ricardo", userDTOs.get(0).getLogin());
        assertEquals( "$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO", userDTOs.get(0).getPassword());
        assertEquals( "81988775423", userDTOs.get(0).getPhone());
        assertEquals( 2L, userDTOs.get(1).getId());
        assertEquals( "Fernando", userDTOs.get(1).getFirstName());
        assertEquals( "Costa", userDTOs.get(1).getLastName());
        assertEquals( LocalDate.of(1999,2,1), userDTOs.get(1).getBirthDate());
        assertEquals( "nando@gmail.com", userDTOs.get(1).getEmail());
        assertNull(userDTOs.get(2).getId());
        assertEquals( "Lucas", userDTOs.get(2).getFirstName());
        assertEquals( "Filho", userDTOs.get(2).getLastName());
        assertEquals( LocalDate.of(2000,11,11), userDTOs.get(2).getBirthDate());
        assertEquals( "luquinhas@gmail.com", userDTOs.get(2).getEmail());
        assertEquals( "luquinhas", userDTOs.get(2).getLogin());
        assertEquals( "$2a$10$.AfRZpLPQKTq.rRdQjtLNenLvrLKZrllYnZUZZOqlqsZsQ8zVCzde", userDTOs.get(2).getPassword());
        assertEquals( "21988826756", userDTOs.get(2).getPhone());
        assertNull(userDTOs.get(2).getLastLogin());
        assertNull(userDTOs.get(2).getCreatedAt());
    }

    @Test
    @DisplayName("Convertendo uma lista de usuários DTOs para uma lista de usuários")
    public void converterUserDTOsToUsers() {
        List<User> users = convertObjects(userDTOs, User.class);

        assertEquals(3, users.size());
        assertEquals( 1L, users.get(0).getId());
        assertEquals( "Ricardo", users.get(0).getFirstName());
        assertEquals( "Lima", users.get(0).getLastName());
        assertEquals(LocalDate.of(1997,12,14), users.get(0).getBirthDate());
        assertEquals( "ricardo@gmail.com", users.get(0).getEmail());
        assertEquals( "ricardo", users.get(0).getLogin());
        assertEquals( "00669988", users.get(0).getPassword());
        assertEquals( "81988775423", users.get(0).getPhone());
        assertEquals( 3L, users.get(1).getId());
        assertEquals( "Ana", users.get(1).getFirstName());
        assertEquals( "Maria", users.get(1).getLastName());
        assertEquals( LocalDate.of(1987,12,14), users.get(1).getBirthDate());
        assertEquals( "aninha@gmail.com", users.get(1).getEmail());
        assertEquals( "ana", users.get(1).getLogin());
        assertEquals( "987654321", users.get(1).getPassword());
        assertEquals( "81986234554", users.get(1).getPhone());
        assertNull( users.get(1).getCreatedAt());
        assertNull( users.get(1).getLastLogin());
        assertNull(users.get(2).getId());
        assertEquals( "Luiza", users.get(2).getFirstName());
        assertEquals( "Maya", users.get(2).getLastName());
        assertEquals( LocalDate.of(1998,6,14), users.get(2).getBirthDate());
        assertEquals( "luiza@gmail.com", users.get(2).getEmail());
        assertEquals( "luzinha", users.get(2).getLogin());
        assertEquals( "123456789", users.get(2).getPassword());
        assertEquals( "87988996655", users.get(2).getPhone());
    }
}
