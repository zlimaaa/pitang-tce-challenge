package br.com.api.pitang.controllers;


import br.com.api.pitang.configs.security.UserDetail;
import static br.com.api.pitang.constants.MessagesConstants.CAR_NOT_FOUND;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LICENSE_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.PERMISSION_DENIED;
import br.com.api.pitang.data.dtos.CarDTO;
import br.com.api.pitang.data.models.Car;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.CarFactory.buildCars;
import static br.com.api.pitang.factory.CarFactory.buildCarsDTOs;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import br.com.api.pitang.repositories.CarRepository;
import br.com.api.pitang.repositories.UserRepository;
import static br.com.api.pitang.utils.DozerConverter.convertObject;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import static java.util.Objects.requireNonNull;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes de integracao do carro")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CarControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private CarRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarController controller;

    private Long carId;
    private Long userId;

    @BeforeAll
    @DisplayName("Preparando para iniciar os testes com um usuario salvo no banco e na sessao")
    public void setUp() {
        mockMvc = standaloneSetup(controller).build();

        User user = buildUsers().get(2);
        user.setEmail("teste@teste.com");
        user.setLogin("testeCar");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        userId = user.getId();

        Car car = buildCars().get(3);
        car.setCreatedAt(LocalDateTime.now());
        car.setUser(user);
        car = repository.save(car);
        carId = car.getId();

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken(new UserDetail(user), null));
        setContext(securityContext);
    }


    @Test
    @Order(1)
    @Transactional
    @DisplayName("Criando carro com sucesso")
    public void createCar() throws Exception {

        mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(buildCarsDTOs().get(2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.year").value(1998))
                .andExpect(jsonPath("$.licensePlate").value("WXY-0935"))
                .andExpect(jsonPath("$.color").value("Preto"))
                .andExpect(jsonPath("$.model").value("Celta"));
    }

    @Test
    @Order(2)
    @DisplayName("Erro ao criar carro com placa existente")
    public void errorCreatingCarT01() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setLicensePlate("KJP-8872");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertEquals(LICENSE_ALREADY_EXISTS, errorMessage);
    }

    @Test
    @Order(3)
    @DisplayName("Erro ao criar carro com placa invalida")
    public void errorCreatingCarT02() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setLicensePlate("KJP-882");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(INVALID_FIELDS));
    }

    @Test
    @Order(4)
    @DisplayName("Erro ao criar carro com modelo faltando")
    public void errorCreatingCarT03() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setModel("");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(MISSING_FIELDS));
    }

    @Test
    @Order(5)
    @DisplayName("Erro ao criar carro com ano invalido")
    public void errorCreatingCarT04() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setYear(1884);

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(INVALID_FIELDS));
    }

    @Test
    @Order(6)
    @DisplayName("Erro ao criar carro com ano faltando")
    public void errorCreatingCarT05() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setYear(null);

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(MISSING_FIELDS));
    }

    @Test
    @Order(7)
    @DisplayName("Erro ao criar carro com cor faltando")
    public void errorCreatingCarT06() throws Exception {
        CarDTO carDTO = buildCarsDTOs().get(3);
        carDTO.setColor("            ");

        String errorMessage = requireNonNull(mockMvc.perform(post("/api/cars").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertTrue(errorMessage.contains(MISSING_FIELDS));
    }

    @Test
    @Order(8)
    @DisplayName("Consultando todos os carros do usuario logado")
    public void findAllCars() throws Exception {

        Car car =  buildCars().get(2);
        car.setUsageCounter(2L);
        car.setCreatedAt(LocalDateTime.now());
        car.setUser(User.builder().id(userId).build());
        repository.save(car);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content.[0].year").value(2008))
                .andExpect(jsonPath("$.content.[0].licensePlate").value("GHF-6292"))
                .andExpect(jsonPath("$.content.[0].color").value("Vermelho"))
                .andExpect(jsonPath("$.content.[0].model").value("Uno Miller"))
                .andExpect(jsonPath("$.content.[1].id").value(carId))
                .andExpect(jsonPath("$.content.[1].year").value(1986))
                .andExpect(jsonPath("$.content.[1].licensePlate").value("KJP-8872"))
                .andExpect(jsonPath("$.content.[1].color").value("Azul"))
                .andExpect(jsonPath("$.content.[1].model").value("Fusca 1300cc"));

        repository.deleteById(car.getId());todo
    }

    @Test
    @Order(9)
    @DisplayName("Consultando carro do usario logado pelo id")
    public void findCar() throws Exception {
        mockMvc.perform(get("/api/cars/" + carId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.year").value(1986))
                .andExpect(jsonPath("$.licensePlate").value("KJP-8872"))
                .andExpect(jsonPath("$.color").value("Azul"))
                .andExpect(jsonPath("$.model").value("Fusca 1300cc"));
    }

    @Test
    @Order(10)
    @DisplayName("Atualizando carro do usario logado com sucesso")
    public void updateCar() throws Exception {
        Car car = buildCars().get(3);
        car.setLicensePlate("KKK-0287");
        car.setColor("Roxo");

        CarDTO carDTO = convertObject(car, CarDTO.class);

        mockMvc.perform(put("/api/cars/" + carId).contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.year").value(1986))
                .andExpect(jsonPath("$.licensePlate").value("KKK-0287"))
                .andExpect(jsonPath("$.color").value("Roxo"))
                .andExpect(jsonPath("$.model").value("Fusca 1300cc"));
    }

    @Test
    @Order(11)
    @DisplayName("Erro ao atualizar carro com id inexistente")
    public void errorUpdateCar() throws Exception {
        Car car = buildCars().get(3);
        car.setLicensePlate("KKK-0287");
        car.setColor("Roxo");

        CarDTO carDTO = convertObject(car, CarDTO.class);

        try {
            mockMvc.perform(put("/api/cars/99").contentType(APPLICATION_JSON).content(gson.toJson(carDTO)))
                    .andExpect(status().isNotFound());
        } catch (Exception ex) {
            assertEquals(NestedServletException.class, ex.getClass());
            assertTrue(ex.getMessage().contains(CAR_NOT_FOUND));
        }

    }

    @Test
    @Order(12)
    @DisplayName("Deletando carro do usuario logado")
    public void deleteCar() throws Exception {
        mockMvc.perform(delete("/api/cars/" + carId))
                .andExpect(status().isNoContent());

        assertTrue(repository.findById(carId).isEmpty());
    }

    @Test
    @Order(13)
    @DisplayName("Erro ao deletar carro permissao negada")
    public void errorDeleteCar() throws Exception {
        User user = buildUsers().get(1);
        user.setEmail("delete@teste.com");
        user.setLogin("delete");
        user.setId(null);
        user = userRepository.save(user);

        Car car = buildCars().get(2);
        car.setUser(user);
        car.setLicensePlate("YXZ-4343");
        car.setCreatedAt(LocalDateTime.now());
        car = repository.save(car);

       String errorMessage = requireNonNull(mockMvc.perform(delete("/api/cars/" + car.getId()))
               .andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertEquals(PERMISSION_DENIED, errorMessage);

        userRepository.deleteById(user.getId());
    }

    @AfterAll
    @DisplayName("Deletando o usario criado no banco de dados")
    public void deleteUserAfterFinishTests() {
        userRepository.findDistinctByLogin("testeCar")
                .ifPresent(
                        u -> userRepository.deleteById(u.getId())
                );
    }
}
