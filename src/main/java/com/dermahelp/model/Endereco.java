package com.dermahelp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    @NotEmpty(message = "Logradouro é obrigatório.")
    private String logradouro;

    @Column(nullable = false)
    @NotNull(message = "Numero é obrigatório.")
    private int numero;

    private String complemento;

    @Column(nullable = false)
    @NotEmpty(message = "Cidade é obrigatório.")
    private String cidade;

    @Column(nullable = false)
    @NotEmpty(message = "Estado é obrigatório.")
    private String estado;

    @Column(nullable = false)
    @NotEmpty(message = "CEP é obrigatório.")
    private String cep;

    @OneToOne(mappedBy = "endereco")
    @JsonIgnore
    @ToString.Exclude
    private Consultorio consultorio;

}
