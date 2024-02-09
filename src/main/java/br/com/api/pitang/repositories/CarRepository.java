package br.com.api.pitang.repositories;

import br.com.api.pitang.data.models.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findDistinctByIdAndUserId(Long id, Long userId);
    Long countByLicensePlateAndIdNot(String plate, Long id);
}
