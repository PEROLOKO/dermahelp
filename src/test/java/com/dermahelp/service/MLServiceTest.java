package com.dermahelp.service;

import com.dermahelp.model.ResultForm;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MLServiceTest {

    @Autowired
    MLService mlService;

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    // # Testa o generateResult
    // — Resgata a imagem "tester.jpg"
    // — Faz a chamada POST na API de ML com a imagem
    // — Verifica se um resultado foi retornado
    // — Verifica se o resultado foi melanoma(Cancer)
    public void givenImage_whenGenerateResult_shouldReturnResult() throws IOException, InterruptedException {
        log.info("convertendo imagem de teste para MultipartFile");
        File file = new File("src/test/resources/tester.jpg");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", fileBytes);

        ResultForm resultForm = mlService.generateResult(multipartFile);

        assertNotNull(resultForm);
        assertEquals("melanoma(Cancer)",resultForm.getResult());
    }

}
