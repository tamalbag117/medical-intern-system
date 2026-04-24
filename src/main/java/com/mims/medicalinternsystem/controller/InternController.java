package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.InternDTO;
import com.mims.medicalinternsystem.service.InternService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interns")
public class InternController {

    @Autowired
    private InternService service;

    @PostMapping
    public InternDTO create(@Valid @RequestBody InternDTO dto) {
        return service.createIntern(dto);
    }

    // ✅ Only owner OR admin
    @GetMapping("/profile")
    public InternDTO getProfile(@RequestParam String email) {
        return service.getProfile(email);
    }

    // ✅ Update own profile only
    @PutMapping("/update")
    public InternDTO updateProfile(@RequestParam String email,
                                   @RequestBody InternDTO dto) {
        return service.updateProfile(email, dto);
    }
}
