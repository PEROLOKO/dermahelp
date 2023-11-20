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
    @Column(name = "fileData", length = 1000)
    private byte[] fileData;

    private String fileName;

    private String fileType;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private String resultado;

    @Column(nullable = false, length = 500)
    private String info;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

}
