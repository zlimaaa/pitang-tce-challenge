package br.com.api.pitang.repositories;


import br.com.api.pitang.data.models.Car;
import static br.com.api.pitang.factory.CarFactory.buildCars;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@DisplayName("Testes unitarios de persistencia do carro")
public class CarRepositoryTest {

    @Autowired
    private CarRepository repository;

    @Autowired
    private UserRepository userRepository;

    private Car car;

    @BeforeAll
    @DisplayName("Preparando para iniciar os testes com carros salvos no banco")
    public void setUp() {
        userRepository.save(buildUsers().get(0));
        car = repository.save(buildCars().get(0));
        repository.save(buildCars().get(1));
    }

    @Test
    @Order(1)
    @DisplayName("Criando carro")
    public void createCar() {
        Car car = buildCars().get(2);
        car.setCreatedAt(LocalDateTime.now());
        car.setUser(this.car.getUser());
        car = repository.save(car);

        assertNotNull(car.getId());
        assertNotNull(car.getUser());
        assertEquals(2008, car.getYear());
        assertEquals("Uno Miller", car.getModel());
        assertEquals("Vermelho", car.getColor());
        assertEquals("GHF-6292", car.getLicensePlate());
        assertNotNull(car.getCreatedAt());
    }

    @Test
    @Order(2)
    @DisplayName("Contando carros pela placa")
    public void countByLicensePlate() {
        Long countCars = repository.countByLicensePlateAndIdNot(car.getLicensePlate(), car.getId());

        assertEquals(0L, countCars);

        countCars = repository.countByLicensePlateAndIdNot("PTG-7622", car.getId());

        assertEquals(1L, countCars);
    }

    @Test
    @Order(3)
    @DisplayName("Consultando carro pelo id")
    public void findById() {
        Car car = repository.findById(this.car.getId()).get();

        assertEquals(1L, car.getId());
        assertNotNull(car.getUser());
        assertEquals(2024, car.getYear());
        assertEquals("Corolla XLS", car.getModel());
        assertEquals("Preto", car.getColor());
        assertEquals("PLK-6721", car.getLicensePlate());
        assertNotNull(car.getCreatedAt());
    }

    @Test
    @Order(4)
    @DisplayName("Atualizando o contador de utilizacoes")
    public void updateUsageCounter() {

        assertEquals(15L, car.getUsageCounter());

        repository.updateUsageCounter(car.getId(), car.getUser().getId());

        Car carUpdated = repository.findById(car.getId()).get();

        assertEquals(16L, carUpdated.getUsageCounter());

    }


    @Test
    @Order(5)
    @DisplayName("Listando todos os carros com a regra do bonus stage")
    public void findAllWithOrderBy() {

        Sort sort = by(desc("usageCounter"), asc("model"));
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<Car> cars = repository.findAllByUserId(car.getUser().getId(), pageable);

        assertEquals(2, cars.getTotalElements());
        assertNotNull(cars.getContent().get(0).getId());
        assertEquals(2022, cars.getContent().get(0).getYear());
        assertEquals("BMW GS 1200", cars.getContent().get(0).getModel());
        assertEquals("BRANCA", cars.getContent().get(0).getColor());
        assertEquals("PTG-7622", cars.getContent().get(0).getLicensePlate());
        assertEquals(car.getId(), cars.getContent().get(1).getId());
        assertEquals(2024, cars.getContent().get(1).getYear());
        assertEquals("Corolla XLS", cars.getContent().get(1).getModel());
        assertEquals("Preto", cars.getContent().get(1).getColor());
        assertEquals("PLK-6721", cars.getContent().get(1).getLicensePlate());

    }


    @Test
    @Order(6)
    @DisplayName("Deletando carro pelo id")
    public void deleteById() {
        repository.deleteById(car.getId());

        try {
            repository.findById(car.getId());
        } catch (Exception ex) {
            assertEquals(NoSuchElementException.class, ex.getClass());
            assertEquals("No value present", ex.getMessage());
        }
    }

}
