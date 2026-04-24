package com.mims.medicalinternsystem.mapper;

import com.mims.medicalinternsystem.dto.InternDTO;
import com.mims.medicalinternsystem.entity.Doctor;
import com.mims.medicalinternsystem.entity.Intern;

public class InternMapper {

    // 🔁 ENTITY → DTO
    public static InternDTO toDTO(Intern intern) {

        if (intern == null) return null; // ✅ safety

        InternDTO dto = new InternDTO();

        dto.setId(intern.getId());
        dto.setName(intern.getName());
        dto.setEmail(intern.getEmail());

        // ✅ Safe relationship mapping
        Doctor doctor = intern.getDoctor();
        if (doctor != null && doctor.getId() != null) {
            dto.setDoctorId(doctor.getId());
        }

        return dto;
    }

    // 🔁 DTO → ENTITY (basic fields only)
    public static Intern toEntity(InternDTO dto) {

        if (dto == null) return null; // ✅ safety

        Intern intern = new Intern();

        intern.setName(dto.getName());
        intern.setEmail(dto.getEmail());

        // ❗ DO NOT set doctor here
        // 👉 Doctor must be set in Service layer (business logic)

        return intern;
    }
}