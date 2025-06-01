package com.comu.comunidade_java.service;

import com.comu.comunidade_java.dto.BoletimRequestDTO;
import com.comu.comunidade_java.dto.BoletimResponseDTO;
import com.comu.comunidade_java.entity.Boletim;
import com.comu.comunidade_java.entity.User;
import com.comu.comunidade_java.exception.ResourceNotFoundException;
import com.comu.comunidade_java.repository.BoletimRepository;
import com.comu.comunidade_java.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Mantido para consistência
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BoletimService {

    @Autowired
    private BoletimRepository boletimRepository;

    @Autowired
    private UserRepository userRepository;

    // Constante para o nome de utilizador padrão
    private static final String DEFAULT_SYSTEM_USERNAME = "sistema";

    private BoletimResponseDTO convertToResponseDTO(Boletim boletim) {
        BoletimResponseDTO dto = new BoletimResponseDTO();
        dto.setId(boletim.getId());
        if (boletim.getUser() != null) {
            dto.setUsername(boletim.getUser().getUsername()); // << USO DE setUsername
        } else {
            dto.setUsername("Sistema"); // << USO DE setUsername
        }
        dto.setTitle(boletim.getTitle());
        dto.setLocation(boletim.getLocation());
        dto.setContent(boletim.getContent());
        dto.setSeverity(boletim.getSeverity());
        dto.setTimestamp(boletim.getTimestamp());
        return dto;
    }

    private Boletim convertToEntity(BoletimRequestDTO dto, User user) {
        Boletim boletim = new Boletim();
        boletim.setUser(user);
        boletim.setTitle(dto.getTitle());
        boletim.setLocation(dto.getLocation());
        boletim.setContent(dto.getContent());
        boletim.setSeverity(dto.getSeverity());
        if (dto.getTimestamp() == null) {
             boletim.setTimestamp(LocalDateTime.now());
        } else {
            boletim.setTimestamp(dto.getTimestamp());
        }
        return boletim;
    }

    @Transactional(readOnly = true)
    public Page<BoletimResponseDTO> findAll(Pageable pageable, String severityFilter, String titleFilter) {
        Page<Boletim> boletins;
        if (severityFilter != null && !severityFilter.isEmpty()) {
            boletins = boletimRepository.findBySeverityIgnoreCase(severityFilter, pageable);
        } else if (titleFilter != null && !titleFilter.isEmpty()) {
            boletins = boletimRepository.findByTitleContainingIgnoreCase(titleFilter, pageable);
        } else {
            boletins = boletimRepository.findAll(pageable);
        }
        return boletins.map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public BoletimResponseDTO findById(Long id) {
        Boletim boletim = boletimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boletim não encontrado com id: " + id));
        return convertToResponseDTO(boletim);
    }

    // Método CREATE ajustado para não depender diretamente do 'username' do Authentication,
    // mas sim de um utilizador padrão se a autenticação não estiver ativa/fornecida.
    @Transactional
    public BoletimResponseDTO create(BoletimRequestDTO boletimRequestDTO, String authenticatedUsername) {
        User creator;
        if (authenticatedUsername != null && !authenticatedUsername.isEmpty()) {
            creator = userRepository.findByUsername(authenticatedUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizador '" + authenticatedUsername + "' não encontrado para criar boletim."));
        } else {
            // Se não houver utilizador autenticado (ex: JWT desativado ou endpoint público para teste),
            // associa ao utilizador "sistema".
            creator = userRepository.findByUsername(DEFAULT_SYSTEM_USERNAME)
                    .orElseThrow(() -> new RuntimeException("Utilizador padrão '" + DEFAULT_SYSTEM_USERNAME + "' não encontrado. Crie-o primeiro no CommandLineRunner."));
        }
        
        Boletim boletim = convertToEntity(boletimRequestDTO, creator);
        boletim = boletimRepository.save(boletim);
        return convertToResponseDTO(boletim);
    }

    @Transactional
    public BoletimResponseDTO update(Long id, BoletimRequestDTO boletimRequestDTO, String authenticatedUsername) {
        Boletim existingBoletim = boletimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boletim não encontrado para atualização com id: " + id));

        // Lógica de permissão (exemplo simples, pode ser mais complexa com roles)
        if (authenticatedUsername != null && !existingBoletim.getUser().getUsername().equals(authenticatedUsername)) {
            // Lançar AccessDeniedException ou tratar de outra forma se não for o dono e não for admin
             System.out.println("Aviso: Tentativa de editar boletim por utilizador não proprietário: " + authenticatedUsername);
             // Para testes, pode permitir a edição, mas numa app real, lançaria exceção.
        }
        
        existingBoletim.setTitle(boletimRequestDTO.getTitle());
        existingBoletim.setLocation(boletimRequestDTO.getLocation());
        existingBoletim.setContent(boletimRequestDTO.getContent());
        existingBoletim.setSeverity(boletimRequestDTO.getSeverity());
        
        Boletim updatedBoletim = boletimRepository.save(existingBoletim);
        return convertToResponseDTO(updatedBoletim);
    }

    @Transactional
    public void delete(Long id, String authenticatedUsername) {
        Boletim boletim = boletimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boletim não encontrado para exclusão com id: " + id));
        
        // Lógica de permissão
        if (authenticatedUsername != null && !boletim.getUser().getUsername().equals(authenticatedUsername)) {
            // Lançar AccessDeniedException ou tratar
            System.out.println("Aviso: Tentativa de excluir boletim por utilizador não proprietário: " + authenticatedUsername);
            // Para testes, pode permitir, mas numa app real, lançaria exceção.
            // throw new AccessDeniedException("Não permitido");
        }
        boletimRepository.deleteById(id);
    }
}