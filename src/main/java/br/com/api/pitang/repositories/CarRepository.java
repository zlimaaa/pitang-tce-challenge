package br.com.api.pitang.repositories;

import br.com.api.pitang.data.models.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

    Page<Car> findAllByUserId(Long userId, Pageable pageable);

    Long countByLicensePlateAndIdNot(String plate, Long id);
}
