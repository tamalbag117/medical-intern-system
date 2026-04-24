package com.mims.medicalinternsystem.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InternDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String email;



    private Long doctorId; // instead of full object
}
