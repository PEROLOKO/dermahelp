package com.dermahelp.controller;

import com.dermahelp.model.Consultorio;
import com.dermahelp.repository.ConsultorioRepository;
import com.dermahelp.repository.EnderecoRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("dermahelp/api/consultorio")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Consultório", description = "Consultório cadastrado na DermaHelp")
public class ConsultorioController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ConsultorioRepository consultorioRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @PostMapping
    @Operation(
            summary = "Cadastro de um Consultório",
            description = "Cadastra um Consultório no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consultório cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@RequestBody @Valid Consultorio consultorio) {
        log.info("salvando consultorio");
        log.info("salvando endereço");
        consultorioRepository.save(consultorio);
        enderecoRepository.save(consultorio.getEndereco());
        log.info("salvo consultorio: "+consultorio.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(consultorio);
    }

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar Consultórios",
            description = "Retorna todos Consultórios cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultórios listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Consultorio> listar(@PageableDefault(size = 5) Pageable pageable) {
        log.info("recuperando todos Consultórios");
        var list = consultorioRepository.findAll();
        log.info("criando paginação");
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), list.size()));
        log.info("retornando Consultórios");
        return new PageImpl<Consultorio>(list.subList(start, end), pageable, list.size());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar Consultório",
            description = "Busca um Consultório por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultório detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Consultório com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Consultorio> index(@PathVariable Long id) {
        log.info("buscando Consultório " + id);
        var result = consultorioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultório não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar Médico",
            description = "Deleta um Médico por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consultório deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Consultório com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Consultorio> destroy(@PathVariable Long id){
        log.info("deletando consultório " + id);
        var result = consultorioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultório não Encontrado"));
        enderecoRepository.delete(result.getEndereco());
        consultorioRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Editar Médico",
            description = "Editar um Médico por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médico atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Médico com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Consultorio> update(@PathVariable Long id, @RequestBody @Valid Consultorio consultorio){
        log.info("atualizando usuario "+id);
        var result = consultorioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        consultorio.setId(id);
        consultorio.setConsultaList(result.getConsultaList());
        consultorioRepository.save(consultorio);
        enderecoRepository.save(consultorio.getEndereco());
        return ResponseEntity.ok(consultorio);
    }

}
