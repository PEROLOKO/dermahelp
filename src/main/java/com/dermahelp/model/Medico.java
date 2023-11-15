package com.dermahelp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    @NotEmpty(message = "Nome é obrigatório.")
    private String nome;

    @Column(nullable = false)
    @NotEmpty(message = "CRM é obrigatório.")
    private String crm;

    @Column(nullable = false)
    @NotEmpty(message = "Email é obrigatório.")
    @Email(message = "O Email precisa ser válido")
    private String email;

    @OneToMany(mappedBy = "medico")
    @JsonIgnore
    @ToString.Exclude
    private List<Consulta> consultaList;

}
