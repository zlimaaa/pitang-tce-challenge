package br.com.api.pitang.factory;

import br.com.api.pitang.data.dtos.CarDTO;
import br.com.api.pitang.data.models.Car;
import br.com.api.pitang.data.models.User;
import java.time.LocalDateTime;
import static java.util.Arrays.asList;
import java.util.List;

public class CarFactory {

    public static List<Car> buildCars() {
        return asList(
                Car.builder()
                        .id(1L)
                        .year(2024)
                        .model("Corolla XLS")
                        .color("Preto")
                        .licensePlate("PLK-6721")
                        .createdAt(LocalDateTime.now())
                        .user(User.builder().id(1L).build())
                        .build(),
                Car.builder()
                        .id(2L)
                        .year(2022)
                        .model("BMW GS 1200")
                        .color("BRANCA")
                        .licensePlate("PTG-7622")
                        .createdAt(LocalDateTime.now())
                        .user(User.builder().id(1L).build())
                        .build(),
                Car.builder()
                        .year(2008)
                        .model("Uno Miller")
                        .color("Vermelho")
                        .licensePlate("GHF-6292")
                        .build(),
                Car.builder()
                        .year(1986)
                        .model("Fusca 1300cc")
                        .color("Azul")
                        .licensePlate("KJP-8872")
                        .build(),
                Car.builder()
                        .id(3L)
                        .year(2015)
                        .model("Toyota Etios Sedan")
                        .color("Prata")
                        .licensePlate("OQA-6400")
                        .createdAt(LocalDateTime.now())
                        .user(User.builder().id(5L).build())
                        .build()
        );
    }


    public static List<CarDTO> buildCarsDTOs() {
        return asList(
                CarDTO.builder()
                        .id(1L)
                        .year(2024)
                        .model("Corolla XLS")
                        .color("Preto")
                        .licensePlate("PLK-6721")
                        .build(),
                CarDTO.builder()
                        .id(2L)
                        .year(2019)
                        .model("MT 03")
                        .color("Cinza")
                        .licensePlate("QJO-3762")
                        .build(),
                CarDTO.builder()
                        .id(null)
                        .year(1998)
                        .model("Celta")
                        .color("Preto")
                        .licensePlate("WXY-0935")
                        .build(),
                CarDTO.builder()
                        .year(2023)
                        .model("Honda XRE 300 Sahara")
                        .color("Vermelho")
                        .licensePlate("YHJ-0866")
                        .build()
                );
    }
}
