package com.dermahelp.config;

import com.dermahelp.model.Consulta;
import com.dermahelp.model.Consultorio;
import com.dermahelp.model.Endereco;
import com.dermahelp.model.Medico;
import com.dermahelp.repository.ConsultorioRepository;
import com.dermahelp.repository.EnderecoRepository;
import com.dermahelp.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    ConsultorioRepository consultorioRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        Medico medico1 = Medico.builder().nome("Denair Carino Vieira").email("doc.denair.vieira@gmail.com").crm("388371606609").build();
        Medico medico2 = Medico.builder().nome("Gleyce Valansuela Nespoli").email("doc.gleyce.nespoli@gmail.com").crm("244112180795").build();

        medicoRepository.saveAll(List.of(medico1, medico2));

        Endereco endereco1 = Endereco.builder().cep("02418110").logradouro("Rua Coronel Antônio Agostinho Bezerra").estado("SP").cidade("São Paulo").numero(35).build();
        Endereco endereco2 = Endereco.builder().cep("02363080").logradouro("Rua Hortência").estado("SP").cidade("São Paulo").numero(447).build();

        Consultorio consultorio1 = Consultorio.builder().nome("Hospital Coronel Antônio").cnpj("66753489000180").endereco(endereco1).build();
        Consultorio consultorio2 = Consultorio.builder().nome("Consultório Hortência").cnpj("13405845000109").endereco(endereco2).build();

        consultorioRepository.saveAll(List.of(consultorio1, consultorio2));
        enderecoRepository.saveAll(List.of(endereco1, endereco2));

    }

}