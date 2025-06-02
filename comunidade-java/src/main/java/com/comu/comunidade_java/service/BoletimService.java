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
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BoletimService {

    @Autowired
    private BoletimRepository boletimRepository;

    @Autowired
    private UserRepository userRepository;

    
    private static final String DEFAULT_SYSTEM_USERNAME = "sistema";

    private BoletimResponseDTO convertToResponseDTO(Boletim boletim) {
        BoletimResponseDTO dto = new BoletimResponseDTO();
        dto.setId(boletim.getId());
        if (boletim.getUser() != null) {
            dto.setUsername(boletim.getUser().getUsername()); 
        } else {
            dto.setUsername("Sistema"); 
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

    
    @Transactional
    public BoletimResponseDTO create(BoletimRequestDTO boletimRequestDTO, String authenticatedUsername) {
        User creator;
        if (authenticatedUsername != null && !authenticatedUsername.isEmpty()) {
            creator = userRepository.findByUsername(authenticatedUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizador '" + authenticatedUsername + "' não encontrado para criar boletim."));
        } else {
            
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

        
        if (authenticatedUsername != null && !existingBoletim.getUser().getUsername().equals(authenticatedUsername)) {
            
             System.out.println("Aviso: Tentativa de editar boletim por utilizador não proprietário: " + authenticatedUsername);
             
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
        
       
        if (authenticatedUsername != null && !boletim.getUser().getUsername().equals(authenticatedUsername)) {
            
            System.out.println("Aviso: Tentativa de excluir boletim por utilizador não proprietário: " + authenticatedUsername);
            
        }
        boletimRepository.deleteById(id);
    }
}