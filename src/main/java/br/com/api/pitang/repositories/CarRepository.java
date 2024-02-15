package br.com.api.pitang.repositories;

import br.com.api.pitang.data.models.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Long> {

    Page<Car> findAllByUserId(Long userId, Pageable pageable);

    Long countByLicensePlateAndIdNot(String plate, Long id);

    @Modifying
    @Query("update Car set usageCounter = usageCounter + 1 where id = :id and user.id = :userId")
    void updateUsageCounter(@Param(value = "id") Long id, @Param(value = "userId") Long userId);
}
