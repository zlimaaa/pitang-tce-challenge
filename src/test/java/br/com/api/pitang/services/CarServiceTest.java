package br.com.api.pitang.services;


import br.com.api.pitang.configs.security.UserDetail;
import static br.com.api.pitang.constants.MessagesConstants.CAR_NOT_FOUND;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LICENSE_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.PERMISSION_DENIED;
import br.com.api.pitang.data.dtos.CarDTO;
import br.com.api.pitang.data.models.Car;
import br.com.api.pitang.exceptions.ValidationException;
import static br.com.api.pitang.factory.CarFactory.buildCars;
import static br.com.api.pitang.factory.CarFactory.buildCarsDTOs;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.CarRepository;
import java.time.Year;
import static java.util.Arrays.asList;
import java.util.List;
import static java.util.Optional.of;
import javax.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes unitarios do carro")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CarServiceTest {

    @Autowired
    private CarService service;

    @MockBean
    private CarRepository repository;

    @BeforeAll
    @DisplayName("Preparando para iniciar os testes com um usuario salvo na sessao")
    public void setUp() {
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(new UserDetail(buildUsers().get(0)),null));
        setContext(securityContext);
    }

    @Test
    @Order(1)
    @DisplayName("Criando um carro")
    public void createCar() {
        when(repository.countByLicensePlateAndIdNot("PLK6721", 0L)).thenReturn(0L);
        when(repository.save(any(Car.class))).thenReturn(buildCars().get(0));

        CarDTO carDTO = buildCarsDTOs().get(0);
        carDTO.setId(null);

        carDTO = service.save(carDTO);

        assertEquals(1L, carDTO.getId());
        assertEquals(2024, carDTO.getYear());
        assertEquals("Corolla XLS", carDTO.getModel());
        assertEquals("Preto", carDTO.getColor());
        assertEquals("PLK-6721", carDTO.getLicensePlate());

    }

    @Test
    @Order(2)
    @DisplayName("Erro ao criar um carro com placa ja existente")
    public void errorCreatingCarT01() {
        when(repository.countByLicensePlateAndIdNot("WXY-0935", 0L)).thenReturn(1L);

        try {
            service.save(buildCarsDTOs().get(2));
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(LICENSE_ALREADY_EXISTS, ex.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Erro ao criar um carro sem informar placa")
    public void errorCreatingCarT02() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setLicensePlate(null);

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Erro ao criar um carro com placa invalida")
    public void errorCreatingCarT03() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setLicensePlate("PGK-202");

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Erro ao criar um carro com ano invalido")
    public void errorCreatingCarT04() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setYear(1880);

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Erro ao criar um carro sem informar o ano")
    public void errorCreatingCarT05() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setYear(null);

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Erro ao criar um carro sem informar o modelo")
    public void errorCreatingCarT06() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setModel("                ");

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Erro ao criar um carro sem informar a cor")
    public void errorCreatingCarT07() {
        CarDTO carDTO = buildCarsDTOs().get(2);
        carDTO.setColor("");

        try {
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(MISSING_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Consultanto carro pelo id e id do usuario")
    public void findCarById() {
        when(repository.findById(1L)).thenReturn(of(buildCars().get(0)));

        CarDTO carDTO = service.findById(1L);

        assertEquals(1L, carDTO.getId());
        assertEquals(2024, carDTO.getYear());
        assertEquals("Corolla XLS", carDTO.getModel());
        assertEquals("Preto", carDTO.getColor());
        assertEquals("PLK-6721", carDTO.getLicensePlate());
    }

    @Test
    @Order(10)
    @DisplayName("Erro ao consultar carro com id inexistente")
    public void errorFindCarById() {
        when(repository.findById(1L)).thenThrow(new EntityNotFoundException(CAR_NOT_FOUND));

        try {
            service.findById(10L);
        } catch (Exception ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
            assertEquals(CAR_NOT_FOUND, ex.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Deletando carro com sucesso")
    public void deleteCar() {
        when(repository.findById(1L)).thenReturn(of(buildCars().get(0)));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @Order(12)
    @DisplayName("Erro ao deletar carro de outro usario")
    public void errorDeleteCar() {
        when(repository.findById(3L)).thenReturn(of(buildCars().get(4)));

        try{
            service.delete(3L);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(PERMISSION_DENIED, ex.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Consultando todos os carros do usuario")
    public void findAll() {
        List<Car> cars = asList(buildCars().get(0), buildCars().get(1));
        Page<Car> page = new PageImpl<>(cars);

        Sort sort = by(desc("usageCounter"), asc("model"));
        when(repository.findAllByUserId(1L, PageRequest.of(0, 10, sort))).thenReturn(page);

        Page<CarDTO> response = service.findAllByUser(0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).getId());
        assertEquals(2024, response.getContent().get(0).getYear());
        assertEquals("PLK-6721", response.getContent().get(0).getLicensePlate());
        assertEquals("Preto", response.getContent().get(0).getColor());
        assertEquals("Corolla XLS", response.getContent().get(0).getModel());
        assertEquals(2L, response.getContent().get(1).getId());
        assertEquals(2022, response.getContent().get(1).getYear());
        assertEquals("PTG-7622", response.getContent().get(1).getLicensePlate());
        assertEquals("BRANCA", response.getContent().get(1).getColor());
        assertEquals("BMW GS 1200", response.getContent().get(1).getModel());
    }

    @Test
    @Order(14)
    @DisplayName("Atualizando carro com sucesso")
    public void updateCar() {
        Car carUpdated = buildCars().get(0);
        carUpdated.setLicensePlate("PEL-7452");
        carUpdated.setYear(2021);

        when(repository.countByLicensePlateAndIdNot("PEL-7452", 1L)).thenReturn(0L);
        when(repository.findById(1L)).thenReturn(of(buildCars().get(0)));
        when(repository.save(any(Car.class))).thenReturn(carUpdated);

        CarDTO carDTO = buildCarsDTOs().get(0);
        carDTO.setLicensePlate("PEL-7452");
        carDTO.setYear(2021);

        carDTO = service.save(carDTO);

        assertEquals(1L, carDTO.getId());
        assertEquals(2021, carDTO.getYear());
        assertEquals("Corolla XLS", carDTO.getModel());
        assertEquals("Preto", carDTO.getColor());
        assertEquals("PEL-7452", carDTO.getLicensePlate());

    }

    @Test
    @Order(15)
    @DisplayName("Erro ao atualizar carro com id inexistente")
    public void errorUpdateCarT01() {
        when(repository.findById(1L)).thenThrow(new EntityNotFoundException(CAR_NOT_FOUND));

        try{
            service.save(buildCarsDTOs().get(1));
        } catch (Exception ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
            assertEquals(CAR_NOT_FOUND, ex.getMessage());
        }
    }

    @Test
    @Order(16)
    @DisplayName("Erro ao atualizar carro com ano invalido")
    public void errorUpdateCarT02() {
        CarDTO carDTO = buildCarsDTOs().get(1);
        carDTO.setYear(Year.now().plusYears(1).getValue());

        try{
            service.save(carDTO);
        } catch (Exception ex) {
            assertEquals(ValidationException.class, ex.getClass());
            assertEquals(INVALID_FIELDS, ex.getMessage());
        }
    }

    @Test
    @Order(17)
    @DisplayName("atualizando o contador de utilizacao do carro")
    public void updateUsageCounter() {
        doNothing().when(repository).updateUsageCounter(1L, buildUsers().get(0).getId());

        service.updateUsageCounter(1L);

        verify(repository, times(1)).updateUsageCounter(1L, buildUsers().get(0).getId());
    }

}
