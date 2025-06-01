package com.comu.comunidade_java.controller;

import com.comu.comunidade_java.dto.BoletimRequestDTO;
import com.comu.comunidade_java.dto.BoletimResponseDTO;
import com.comu.comunidade_java.service.BoletimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Outra forma de obter o utilizador

@RestController
@RequestMapping("/api/boletins")
@Tag(name = "Boletins", description = "API para gestão de Boletins")
// @SecurityRequirement(name = "bearerAuth") // Comente se JWT estiver globalmente desativado para testes
public class BoletimController {

    @Autowired
    private BoletimService boletimService;

    @Operation(summary = "Lista todos os boletins com paginação, ordenação e filtros opcionais")
    @GetMapping
    public ResponseEntity<Page<BoletimResponseDTO>> getAllBoletins(
            @PageableDefault(size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Filtrar por gravidade (ex: Alerta, Cuidado, Info)") @RequestParam(required = false) String severity,
            @Parameter(description = "Filtrar por texto contido no título") @RequestParam(required = false) String title) {
        return ResponseEntity.ok(boletimService.findAll(pageable, severity, title));
    }

    @Operation(summary = "Busca um boletim por ID")
    @GetMapping("/{id}")
    public ResponseEntity<BoletimResponseDTO> getBoletimById(@PathVariable Long id) {
        return ResponseEntity.ok(boletimService.findById(id));
    }

    @Operation(summary = "Cria um novo boletim")
    @PostMapping
    // @PreAuthorize("isAuthenticated()") // Comente se JWT estiver desativado para este endpoint
    public ResponseEntity<BoletimResponseDTO> createBoletim(
            @Valid @RequestBody BoletimRequestDTO boletimRequestDTO,
            Principal principal // Opcional: Spring injeta o principal se autenticado, senão é null
    ) {
        String username = (principal != null) ? principal.getName() : null;
        // Se principal for null, o serviço usará o utilizador "sistema"
        BoletimResponseDTO createdBoletim = boletimService.create(boletimRequestDTO, username);
        return new ResponseEntity<>(createdBoletim, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza um boletim existente")
    @PutMapping("/{id}")
    // @PreAuthorize("isAuthenticated()") // Comente se JWT estiver desativado
    public ResponseEntity<BoletimResponseDTO> updateBoletim(
            @PathVariable Long id, 
            @Valid @RequestBody BoletimRequestDTO boletimRequestDTO,
            Principal principal
    ) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(boletimService.update(id, boletimRequestDTO, username));
    }

    @Operation(summary = "Deleta um boletim por ID")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // Comente se JWT estiver desativado ou para simplificar permissões
    public ResponseEntity<Void> deleteBoletim(@PathVariable Long id, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        boletimService.delete(id, username);
        return ResponseEntity.noContent().build();
    }
}