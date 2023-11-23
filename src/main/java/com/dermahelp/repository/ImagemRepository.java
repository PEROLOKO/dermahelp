package com.dermahelp.repository;

import com.dermahelp.model.Imagem;
import com.dermahelp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findByUsuario(Usuario usuario);
}