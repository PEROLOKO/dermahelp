package com.dermahelp.controller;

import com.dermahelp.model.Imagem;
import com.dermahelp.model.Medico;
import com.dermahelp.model.Usuario;
import com.dermahelp.repository.MedicoRepository;
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
@RequestMapping("dermahelp/api/medico")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Médico", description = "Médico cadastrado na DermaHelp")
public class MedicoController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MedicoRepository medicoRepository;

    @PostMapping
    @Operation(
            summary = "Cadastro de um médico",
            description = "Cadastra um médico no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médico cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@RequestBody @Valid Medico medico) {
        log.info("salvando medico");
        medicoRepository.save(medico);
        log.info("salvo medico: "+medico.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(medico);
    }

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar Médicos",
            description = "Retorna todos Médicos cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médicos listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Medico> listar(@PageableDefault(size = 5) Pageable pageable) {
        log.info("recuperando todos médicos");
        var list = medicoRepository.findAll();
        log.info("criando paginação");
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), list.size()));
        log.info("retornando médicos");
        return new PageImpl<Medico>(list.subList(start, end), pageable, list.size());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar médico",
            description = "Busca um médico por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médico detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Médico com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Medico> index(@PathVariable Long id) {
        log.info("buscando usuario " + id);
        var result = medicoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar Médico",
            description = "Deleta um Médico por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Médico deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Médico com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Medico> destroy(@PathVariable Long id){
        log.info("deletando médico " + id);
        var result = medicoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não Encontrado"));
        medicoRepository.delete(result);
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
    public ResponseEntity<Medico> update(@PathVariable Long id, @RequestBody @Valid Medico medico){
        log.info("atualizando usuario "+id);
        var result = medicoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        medico.setId(id);
        medico.setConsultaList(result.getConsultaList());
        medicoRepository.save(medico);
        return ResponseEntity.ok(medico);
    }

}
