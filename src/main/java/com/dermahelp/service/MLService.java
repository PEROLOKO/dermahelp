package com.dermahelp.service;

import com.dermahelp.model.ResultForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MLService {

    Logger log = LoggerFactory.getLogger(getClass());

    @Value("${dermahelp.ml.host}")
    private String mlhost;
    @Value("${dermahelp.ml.port}")
    private String mlport;

    ObjectMapper objectMapper = new ObjectMapper();

    public ResultForm generateResult(MultipartFile multipartFile) throws IOException, InterruptedException {

        log.info("convertendo arquivo multipartes");
        String path = "src/main/resources/" + multipartFile.getOriginalFilename();

        File file = new File(path);

        log.info("salvando arquivo: "+path);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }

        String endpoint = mlhost + ":" + mlport + "/api/runmodel";
        log.info("fazendo requisição no endpoint: "+endpoint);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addBinaryBody("pic", file);
        HttpEntity entity = builder.build();

        HttpPost post = new HttpPost(endpoint);
        post.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post)) {
            String responseString = new BasicHttpClientResponseHandler().handleResponse(response);
            log.info(responseString);

            log.info("deletando arquivo temporário de: "+path);
            Files.delete(Path.of(path));

            return objectMapper.readValue(responseString,ResultForm.class);
        }
    }
}
