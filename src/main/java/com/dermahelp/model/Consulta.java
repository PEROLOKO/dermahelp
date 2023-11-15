package com.dermahelp.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cd_medico")
    private Medico medico;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cd_consultorio")
    private Consultorio consultorio;

}
