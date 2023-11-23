package com.dermahelp.controller;

import com.dermahelp.model.FileUploadForm;
import com.dermahelp.model.Imagem;
import com.dermahelp.model.ResultForm;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.ImagemRepository;
import com.dermahelp.repository.UsuarioRepository;
import com.dermahelp.service.ImageUtil;
import com.dermahelp.service.MLService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("dermahelp/api/imagem")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Imagem", description = "Imagem tirada pelo usuário para indentificação")
public class ImagemController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ImagemRepository imagemRepository;

    @Autowired
    MLService mlService;

    @Autowired
    TokenService tokenService;

    @PostMapping
    @CrossOrigin
    @Operation(
            summary = "Cadastro de uma imagem",
            description = "Cadastra uma imagem no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imagem cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@RequestHeader("Authorization") String header, @RequestParam("pic") MultipartFile file) throws IOException, InterruptedException {
        log.info("cadastrando imagem");
        log.info("carregando arquivo de imagem");
        log.info("carregado imagem: "+file.getOriginalFilename());
        log.info("procurando usuario do token");
        var usuarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("econtrado usuario: "+usuarioResult.toString());
        Imagem imagem = new Imagem();
        imagem.setFileName(file.getOriginalFilename());
        imagem.setFileType(file.getContentType());
        imagem.setFileData(ImageUtil.compressImage(file.getBytes()));
        imagem.setData(LocalDateTime.now());
        imagem.setUsuario(usuarioResult);
        ResultForm resultado = mlService.generateResult(file);
        imagem.setResultado(resultado.getResult());
        imagem.setInfo(resultado.getInfo());
        log.info("salvando imagem");
        imagemRepository.save(imagem);
        log.info("imagem salva");
        return ResponseEntity.status(HttpStatus.CREATED).body(imagem);
    }

    @GetMapping("{id}")
    @CrossOrigin
    @Operation(
            summary = "Ver Imagem",
            description = "Busca uma Imagem por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem exibida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Imagem com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<?>  getImagemById(@PathVariable Long id){
        log.info("recuperando dados da imagem de id#"+id);
        Optional<Imagem> dbImage = imagemRepository.findById(id);
        log.info("retornado dados da imagem de id#"+id);
        log.info("descompactando arquivo de imagem");
        byte[] image = ImageUtil.decompressImage(dbImage.get().getFileData());
        log.info("retornando imagem");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/info/{id}")
    @CrossOrigin
    @Operation(
            summary = "Ver informações da Imagem",
            description = "Busca informações de uma Imagem pelo id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem detalhada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Imagem com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<?>  getImagemInfoByNId(@PathVariable Long id){
        log.info("recuperando dados da imagem de id#"+id);
        var dbImage = imagemRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não Encontrado"));
        log.info("retornado dados da imagem de id#"+id);
        log.info("criando retorno com apenas as informações da imagem");
        Imagem imagem = Imagem.builder()
                .data(dbImage.getData())
                .id(id)
                .fileName(dbImage.getFileName())
                .fileType(dbImage.getFileType())
                .resultado(dbImage.getResultado())
                .info(dbImage.getInfo())
                .usuario(dbImage.getUsuario())
                .build();
        log.info("retornando informações da imagem");
        return ResponseEntity.status(HttpStatus.OK)
                .body(imagem);
    }

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar Imagens",
            description = "Retorna todas Imagens cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagens listadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Imagem> listar(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable) {
        log.info("recuperando todas imagens do usuario");
        log.info("procurando usuario do token");
        var usuarioResult = tokenService.validate(tokenService.getToken(header));
        var list = imagemRepository.findByUsuario(usuarioResult);
        log.info("tirando dados da imagen para retornar a lista");
        for (Imagem imagem : list) {
            imagem.setFileData(null);
        }
        log.info("criando paginação");
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), list.size()));
        log.info("retornando imagens");
        return new PageImpl<Imagem>(list.subList(start, end), pageable, list.size());
    }

}
