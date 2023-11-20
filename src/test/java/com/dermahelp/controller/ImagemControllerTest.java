package com.dermahelp.controller;

import com.dermahelp.model.Credencial;
import com.dermahelp.model.Token;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.ImagemRepository;
import com.dermahelp.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.javafaker.Faker;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
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

import java.io.File;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImagemControllerTest {

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
    @Autowired
    private ImagemRepository imagemRepository;

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
    // # Testa o POST em imagem
    // — Cria o token com createToken()
    // — Resgata a imagem "tester.jpg"
    // — Faz a chamada POST na API com a imagem e o id do usuário criado com o token
    // — Verifica se o código foi 200
    // — Verifica se a imagem foi criada
    public void withUsuarioAndImageFile_whenImagePost_shouldBeSaved() throws Exception {

        var token = createToken();
        log.info(token.token());

        File file = new File("src/test/resources/tester.jpg");

        String endpoint = "http://localhost:" + port + "/dermahelp/api/imagem/1";
        log.info("fazendo requisição no endpoint: "+endpoint);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addBinaryBody("file", file);
        org.apache.hc.core5.http.HttpEntity entity = builder.build();

        HttpPost post = new HttpPost(endpoint);
        post.setEntity(entity);
        post.setHeader("Authorization","Bearer "+token.token());

        try (CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post)) {
            String responseString = new BasicHttpClientResponseHandler().handleResponse(response);
            log.info(responseString);

            assertEquals(201, response.getCode());
            assertNotNull(imagemRepository.findById(1L));
        }

    }

}
