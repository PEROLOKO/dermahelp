package com.dermahelp.controller;

import com.dermahelp.model.FileUploadForm;
import com.dermahelp.model.Imagem;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.ImagemRepository;
import com.dermahelp.repository.UsuarioRepository;
import com.dermahelp.service.ImageUtil;
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

    @PostMapping("{idUsuario}")
    @Operation(
            summary = "Cadastro de uma imagem",
            description = "Cadastra uma imagem no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imagem cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@PathVariable Long idUsuario,@ModelAttribute("fileUploadForm") FileUploadForm fileUploadForm) throws IOException {
        MultipartFile file = fileUploadForm.getFile();
        var usuarioResult = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        Imagem imagem = new Imagem();
        imagem.setName(file.getOriginalFilename());
        imagem.setType(file.getContentType());
        imagem.setImageData(ImageUtil.compressImage(file.getBytes()));
        imagem.setData(LocalDateTime.now());
        imagem.setUsuario(usuarioResult);
        // pegar resultado pelo ml
        imagem.setResultado("RESULTADO GERADO PELO ML");
        imagemRepository.save(imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagem);
    }

    @GetMapping("{id}")
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

        Optional<Imagem> dbImage = imagemRepository.findById(id);
        byte[] image = ImageUtil.decompressImage(dbImage.get().getImageData());

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/info/{id}")
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
        var dbImage = imagemRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não Encontrado"));

        Imagem imagem = Imagem.builder()
                .data(dbImage.getData())
                .id(id)
                .name(dbImage.getName())
                .type(dbImage.getType())
                .resultado(dbImage.getResultado())
                .usuario(dbImage.getUsuario())
                .build();

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
    public Page<Imagem> listar(@PageableDefault(size = 5) Pageable pageable) {
        var list = imagemRepository.findAll();
        for (Imagem imagem :
                list) {
            imagem.setImageData(null);
        }
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), list.size()));
        return new PageImpl<Imagem>(list.subList(start, end), pageable, list.size());
    }

}
