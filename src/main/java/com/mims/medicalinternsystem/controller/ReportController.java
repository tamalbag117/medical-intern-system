package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService service;

    // ✅ PDF
    @GetMapping("/activities/pdf")
    public ResponseEntity<byte[]> pdf() throws Exception {

        byte[] data =
                service.exportActivitiesPdf();

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=activities.pdf"
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .body(data);
    }

    // ✅ EXCEL
    @GetMapping("/activities/excel")
    public ResponseEntity<byte[]> excel() throws Exception {

        byte[] data =
                service.exportActivitiesExcel();

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=activities.xlsx"
                )

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )

                .body(data);
    }
}
