package br.com.api.pitang.controllers;


import br.com.api.pitang.data.dtos.CarDTO;
import br.com.api.pitang.services.CarService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "Cars", tags = "Cars")
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService service;

    @PostMapping
    public ResponseEntity<CarDTO> create(@Valid @RequestBody CarDTO car) {
        return new ResponseEntity<>(this.service.save(car), CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CarDTO>> findAllByUser(@RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "5")int pageSize) {
        return ok(this.service.findAllByUser(pageNumber, pageSize));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> update(@PathVariable Long id, @Valid @RequestBody CarDTO car) {
        return ok(this.service.save(car));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> findById(@PathVariable Long id) {
        return ok(this.service.findById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        this.service.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
