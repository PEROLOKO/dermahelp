package com.dermahelp.controller;

import com.dermahelp.model.Credencial;
import com.dermahelp.model.Token;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(getClass());
    Faker faker = new Faker(new Locale("pt-BR"));

    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();
    
    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    PasswordEncoder encoder;

    public Token createToken() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome Teste");
        usuario.setCpf("01699531064");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senhaTeste");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntityCadastro = new HttpEntity<>(objectMapper.writeValueAsString(usuario), headers);
        ResponseEntity<String> responseCadastro = restTemplate.exchange(
                "http://localhost:" + port + "/dermahelp/api/usuario",
                HttpMethod.POST,
                requestEntityCadastro,
                String.class);

        Credencial credencial = new Credencial(usuario.getEmail(),usuario.getSenha());
        HttpEntity<String> requestEntityLogin = new HttpEntity<>(objectMapper.writeValueAsString(credencial), headers);
        ResponseEntity<String> responseLogin = restTemplate.exchange(
                "http://localhost:" + port + "/dermahelp/api/usuario/login",
                HttpMethod.POST,
                requestEntityLogin,
                String.class);

        return objectMapper.readValue(responseLogin.getBody(), Token.class);
    }

    @Test
    // # Testa o método createToken()
    // — Cria um usuário
    // — Efetua o login desse usuário
    // — Retorna o token do login
    // — Verifica se token foi retornado
    // — Verifica se o usuário foi criado
    public void withCreateToken_shouldCreateUsuarioAndToken() throws Exception {

        var token = createToken();
        var usuario = usuarioRepository.findById(1L);

        log.info(token.toString());
        log.info(usuario.toString());

        assertNotNull(token);
        assertNotNull(usuario);

    }

    @Test
    // # Testa o login de usuário
    // — Cria um usuário cadastra
    // — Faz a chamada POST na API para login
    // — Verifica se o código foi 201
    // — Verifica se foi retornado um token
    public void givenCredentials_whenLogin_shouldReturnToken () throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Usuario usuario = new Usuario();
        usuario.setNome("Nome Teste");
        usuario.setCpf("01699531064");
        usuario.setEmail("teste@email.com");
        usuario.setSenha(encoder.encode("senhaTeste"));
        usuarioRepository.save(usuario);

        Credencial credentials = new Credencial("teste@email.com","senhaTeste");

        String requestBody = objectMapper.writeValueAsString(credentials);

        log.info(requestBody);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/dermahelp/api/usuario/login";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(objectMapper.readValue(response.getBody(), Token.class));
    }
    
}
