package br.com.api.pitang.services;

import static br.com.api.pitang.constants.MessagesConstants.CAR_NOT_FOUND;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LICENSE_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.PERMISSION_DENIED;
import br.com.api.pitang.data.dtos.CarDTO;
import br.com.api.pitang.data.models.Car;
import br.com.api.pitang.exceptions.ValidationException;
import br.com.api.pitang.repositories.CarRepository;
import static br.com.api.pitang.utils.DozerConverter.convertObject;
import static br.com.api.pitang.utils.GenericUtils.getUserLogged;
import java.time.LocalDateTime;
import java.time.Year;
import static java.util.Objects.requireNonNull;
import javax.persistence.EntityNotFoundException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarService {

    @Autowired
    private CarRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public CarDTO save(CarDTO carDTO) {
        Car car = convertDTOtoEntity(carDTO);

        validateFields(car);
        unique(car);

        car = repository.save(car);
        return convertEntityToDTO(car);
    }

    public Page<CarDTO> findAllByUser(int pageNumber, int pageSize) {
        Sort sort = by(desc("usageCounter"), asc("model"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Car> cars = repository.findAllByUserId(requireNonNull(getUserLogged()).getId(), pageable);
        return cars.map(this::convertEntityToDTO);
    }

    public CarDTO findById(Long id) {
        return convertEntityToDTO(getCarIfUserHasPermission(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getCarIfUserHasPermission(id);
        repository.deleteById(id);
    }

    /**
     * validacao de campos vazio: licensePlate, model, color, year
     * validacao de campos invalidos: year (menor que 1885 ou ano futuro), licensePlate (TCE-2024)
     * @param car
     * @throws ValidationException caso falhe em alguma das verificacoes acima
     */
    private void validateFields(Car car) {

        if (isBlank(car.getLicensePlate()) ||
                isBlank(car.getModel()) ||
                isBlank(car.getColor()) ||
                car.getYear() == null)
            throw new ValidationException(MISSING_FIELDS);

        if (!isValidCarYear(car.getYear()) ||
                car.getLicensePlate().length() != 8)
            throw new ValidationException(INVALID_FIELDS);

        if (car.getId() == null)
            validateInsert(car);
        else
            validateUpdated(car);

        car.setLicensePlate(car.getLicensePlate().toUpperCase());
    }

    private void validateInsert(Car car) {
        car.setCreatedAt(LocalDateTime.now());
        car.setUser(getUserLogged());
        car.setUsageCounter(0L);
    }

    private void validateUpdated(Car car) {
        Car carSaved = getCarIfUserHasPermission(car.getId());
        car.setCreatedAt(carSaved.getCreatedAt());
        car.setUser(carSaved.getUser());
        car.setUsageCounter(carSaved.getUsageCounter());
    }

    /**
     * procura um carro pelo id e caso o encontre, valida se o carro encontradado
     * pertence ao usuario logado
     * @param id
     * @return Car  caso o carro encontrado pertenca ao usuario logado
     */
    private Car getCarIfUserHasPermission(Long id) {
        Car carSaved = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND));

        if (!carSaved.getUser().getId().equals(requireNonNull(getUserLogged()).getId()))
            throw new ValidationException(PERMISSION_DENIED);

        return carSaved;
    }

    /**
     * validacao de unicidade da placa do carro, verificando se a placa
     * ja existe no banco, mesmo que pertencendo a outro usuario
     * @param car
     * @throws ValidationException caso haja outro carro com a mesma placa
     */
    private void unique(Car car) {
        Long carId = car.getId() == null ? 0L : car.getId();
        Long count = repository.countByLicensePlateAndIdNot(car.getLicensePlate(), carId);

        if (count > 0L)
            throw new ValidationException(LICENSE_ALREADY_EXISTS);
    }

    private boolean isValidCarYear(Integer year) {
        int currentYear = Year.now().getValue();
        return year >= 1885 && year <= currentYear;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUsageCounter(Long carId) {
        repository.updateUsageCounter(carId, requireNonNull(getUserLogged()).getId());
    }

    private Car convertDTOtoEntity(CarDTO carDTO) {
        return convertObject(carDTO, Car.class);
    }

    private CarDTO convertEntityToDTO(Car car) {
        return convertObject(car, CarDTO.class);
    }
}