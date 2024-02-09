package br.com.api.pitang.controllers;


import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.services.UserService;
import static br.com.api.pitang.utils.GenericUtils.getUserLogged;
import io.swagger.annotations.Api;
import static java.util.Objects.requireNonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "User Info", tags = "User Info")
@RequestMapping("/api/me")
public class UserInfoController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<UserDTO> userInfo() {
        return ok(this.service.findById(requireNonNull(getUserLogged()).getId()));
    }
}
