package com.dermahelp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Lob
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;

    private String name;

    private String type;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private String resultado;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

}
