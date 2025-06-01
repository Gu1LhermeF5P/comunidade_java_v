package com.comu.comunidade_java.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data // O Lombok @Data deve gerar getters e setters
public class BoletimResponseDTO {
    private Long id;
    private String username; 
    private String title;
    private String location;
    private String content;
    private String severity;
    private LocalDateTime timestamp;
}