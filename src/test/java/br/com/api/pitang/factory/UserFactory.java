package br.com.api.pitang.factory;

import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.CarFactory.buildCars;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import java.util.List;

public class UserFactory {

    public static List<User> buildUsers() {
        return asList(
                User.builder()
                        .id(1L)
                        .firstName("Ricardo")
                        .lastName("Lima")
                        .email("ricardo@gmail.com")
                        .birthDate(LocalDate.of(1997,12,14))
                        .login("ricardo")
                        .password("$2a$10$AQe5K87Y5CysW4un0eDi5OAncw.zUYqlfsQw7aSEMvtRMxYKM0EwO")
                        .phone("81988775423")
                        .createdAt(LocalDateTime.now())
                        .build(),
                User.builder()
                        .id(2L)
                        .firstName("Fernando")
                        .lastName("Costa")
                        .birthDate(LocalDate.of(1999,2,1))
                        .email("nando@gmail.com")
                        .login("nando01")
                        .password("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC")
                        .phone("11989774271")
                        .createdAt(LocalDateTime.now())
                        .lastLogin(LocalDateTime.now())
                        .build(),
                User.builder()
                        .firstName("Lucas")
                        .lastName("Filho")
                        .birthDate(LocalDate.of(2000,11,11))
                        .email("luquinhas@gmail.com")
                        .login("luquinhas")
                        .password("$2a$10$.AfRZpLPQKTq.rRdQjtLNenLvrLKZrllYnZUZZOqlqsZsQ8zVCzde")
                        .phone("21988826756")
                        .build(),
                User.builder()
                        .firstName("Marcos")
                        .lastName("Pontes")
                        .birthDate(LocalDate.of(1980,10,30))
                        .email("marcos@gmail.com")
                        .login("marcos")
                        .password("$2a$10$.AfRZpLPQKTq.rRdQjtLNenLvrLKZrllYnZUZZOqlqsZsQ8zVCzde")
                        .phone("87987695672")
                        .build(),
                User.builder()
                        .id(5L)
                        .firstName("Otavio")
                        .lastName("Mendes")
                        .birthDate(LocalDate.of(1979,2,1))
                        .email("otavio-m@gmail.com")
                        .login("mendes")
                        .password("$2a$10$A3BtshmFkCkcmWkDLfzA6OoS0xIEVPvc/rh2lbITuzoNqSFHjuizC")
                        .phone("87983772270")
                        .createdAt(LocalDateTime.now())
                        .lastLogin(LocalDateTime.now())
                        .cars(singletonList(buildCars().get(4)))
                        .build()
        );
    }

    public static List<UserDTO> buildUserDTOs() {
        return asList(
                UserDTO.builder()
                        .id(1L)
                        .firstName("Ricardo")
                        .lastName("Lima")
                        .birthDate(LocalDate.of(1997,12,14))
                        .email("ricardo@gmail.com")
                        .login("ricardo")
                        .password("00669988")
                        .phone("81988775423")
                        .createdAt(LocalDateTime.now())
                        .build(),
                UserDTO.builder()
                        .id(3L)
                        .firstName("Ana")
                        .lastName("Maria")
                        .birthDate(LocalDate.of(1987,12,14))
                        .email("aninha@gmail.com")
                        .login("ana")
                        .password("987654321")
                        .phone("81986234554")
                        .build(),
                UserDTO.builder()
                        .id(null)
                        .firstName("Luiza")
                        .lastName("Maya")
                        .birthDate(LocalDate.of(1998,6,14))
                        .email("luiza@gmail.com")
                        .login("luzinha")
                        .password("123456789")
                        .phone("87988996655")
                        .build()
        );
    }

}
