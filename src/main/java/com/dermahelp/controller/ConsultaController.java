package com.dermahelp.controller;

import com.dermahelp.model.Consulta;
import com.dermahelp.model.Medico;
import com.dermahelp.repository.ConsultaRepository;
import com.dermahelp.repository.ConsultorioRepository;
import com.dermahelp.repository.MedicoRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("dermahelp/api/consulta")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Consulta", description = "Consulta feita com a DermaHelp")
public class ConsultaController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ConsultaRepository consultaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    ConsultorioRepository consultorioRepository;

    @Autowired
    TokenService tokenService;

    @PostMapping
    @Operation(
            summary = "Cadastro de uma Consulta",
            description = "Cadastra uma Consulta no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    public ResponseEntity<Object> cadastro(@RequestHeader("Authorization") String header, @RequestBody @Valid Consulta consulta) {
        log.info("cadastrando consulta");
        log.info("procurando usuario do token");
        var usuarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("econtrado usuario: "+usuarioResult.toString());
        log.info("prodcurando medico");
        var medicoResult = medicoRepository.findById(consulta.getMedico().getId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não Encontrado"));
        log.info("prodcurando consultorio");
        var consultorioResult = consultorioRepository.findById(consulta.getConsultorio().getId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultório não Encontrado"));
        consulta.setUsuario(usuarioResult);
        consulta.setMedico(medicoResult);
        consulta.setConsultorio(consultorioResult);
        log.info("salvando consulta");
        consultaRepository.save(consulta);
        return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
    }

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar Consultas",
            description = "Retorna todos Consultas cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultas listadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Consulta> listar(@PageableDefault(size = 5) Pageable pageable) {
        log.info("recuperando todos consultas");
        var list = consultaRepository.findAll();
        log.info("criando paginação");
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), list.size()));
        log.info("retornando consultas");
        return new PageImpl<Consulta>(list.subList(start, end), pageable, list.size());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar consulta",
            description = "Busca uma consulta por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta detalhada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Consulta com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Consulta> index(@PathVariable Long id) {
        log.info("buscando usuario " + id);
        var result = consultaRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar Consulta",
            description = "Deleta uma Consulta por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consulta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Consulta com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Consulta> destroy(@PathVariable Long id){
        log.info("deletando consulta " + id);
        var result = consultaRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não Encontrado"));
        consultaRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

}
