package com.dermahelp.controller;

import com.dermahelp.model.Credencial;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.UsuarioRepository;
import com.dermahelp.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("dermahelp/api/usuario")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Usuário", description = "Usuário da DermaHelp")
public class UsuarioController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping
    @Operation(
            summary = "Cadastro de usuário",
            description = "Cadastra um usuário no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@RequestBody @Valid Usuario usuario) {
        log.info("cadastrando usuario");
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        log.info("cadastrado usuario: "+usuario.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @CrossOrigin
    @PostMapping("login")
    @Operation(
            summary = "Login de usuário",
            description = "Realiza o login de um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Dados inválidos")
    })
    public ResponseEntity<Object> login(@RequestBody Credencial credencial) {
        manager.authenticate(credencial.toAuthentication());
        log.info("credenciais autenticadas");
        var token = tokenService.generateToken(credencial);
        log.info("gerado token");
        return ResponseEntity.ok(token);
    }

}
