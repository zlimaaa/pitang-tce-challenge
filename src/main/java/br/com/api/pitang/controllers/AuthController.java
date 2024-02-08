package br.com.api.pitang.controllers;

import br.com.api.pitang.data.dtos.AuthDTO;
import br.com.api.pitang.services.AuthService;
import io.swagger.annotations.Api;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "SigIn", tags = "SigIn")
@RequestMapping("/api/signin")
public class AuthController {

   @Autowired
   private AuthService service;

    @PostMapping
    public ResponseEntity<Map<Object, Object>> signIn(@Valid @RequestBody AuthDTO authDTO) {
        return ok(this.service.signIn(authDTO));
    }
}
