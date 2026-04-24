package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.InternDTO;
import com.mims.medicalinternsystem.entity.Doctor;
import com.mims.medicalinternsystem.entity.Intern;
import com.mims.medicalinternsystem.exception.ResourceNotFoundException;
import com.mims.medicalinternsystem.mapper.InternMapper;
import com.mims.medicalinternsystem.repository.DoctorRepository;
import com.mims.medicalinternsystem.repository.InternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;



import com.mims.medicalinternsystem.dto.InternDTO;
import com.mims.medicalinternsystem.entity.Intern;
import com.mims.medicalinternsystem.repository.InternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class InternService {

    @Autowired
    private InternRepository repo;

    @Autowired
    private DoctorRepository doctorRepository;

    // 🔐 CREATE (already exists in your project)
    public InternDTO createIntern(InternDTO dto) {
        Intern intern = new Intern();
        intern.setName(dto.getName());
        intern.setEmail(dto.getEmail());
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        intern.setDoctor(doctor);

        repo.save(intern);

        return mapToDTO(intern);
    }

    // 🔐 GET PROFILE (owner OR admin)
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public InternDTO getProfile(String email) {

        Intern intern = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Intern not found"));

        return mapToDTO(intern);
    }

    // 🔐 UPDATE PROFILE (ONLY owner)
    @PreAuthorize("#email == authentication.name")
    public InternDTO updateProfile(String email, InternDTO dto) {

        Intern intern = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Intern not found"));

        intern.setName(dto.getName());

        repo.save(intern);

        return mapToDTO(intern);
    }

    // 🔁 MAPPER
    private InternDTO mapToDTO(Intern intern) {
        InternDTO dto = new InternDTO();
        dto.setName(intern.getName());
        dto.setEmail(intern.getEmail());
        if (intern.getDoctor() != null) {
            dto.setDoctorId(intern.getDoctor().getId());
        }
        return dto;
    }
}