package br.com.api.pitang.controllers;

import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Api(value = "Users", tags = "Users")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody UserDTO user) {
        return new ResponseEntity<>(service.save(user), CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id) {
        return ok(service.findOne(id));
    }
}
