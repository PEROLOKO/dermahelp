package com.dermahelp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Consultorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    @NotEmpty(message = "Nome é obrigatório.")
    private String nome;

    @Column(nullable = false)
    @NotEmpty(message = "CNPJ é obrigatório.")
    private String cnpj;

    @OneToMany(mappedBy = "consultorio")
    @JsonIgnore
    @ToString.Exclude
    private List<Consulta> consultaList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cd_endereco")
    private Endereco endereco;

}
