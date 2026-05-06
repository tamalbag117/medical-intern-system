package com.mims.medicalinternsystem.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.repository.ActivityLogRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ActivityLogRepository repo;

    // ✅ PDF EXPORT
    public byte[] exportActivitiesPdf() throws Exception {

        List<ActivityLog> activities = repo.findAll();

        Document document = new Document();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD
        );

        Paragraph title =
                new Paragraph(
                        "Medical Activity Report",
                        font
                );

        title.setSpacingAfter(20);

        document.add(title);

        PdfPTable table = new PdfPTable(4);

        table.addCell("Patient");
        table.addCell("Task");
        table.addCell("Status");
        table.addCell("Intern");

        for (ActivityLog a : activities) {

            table.addCell(a.getPatientName());

            table.addCell(a.getTask());

            table.addCell(a.getStatus());

            table.addCell(a.getInternEmail());
        }

        document.add(table);

        document.close();

        return out.toByteArray();
    }

    // ✅ EXCEL EXPORT
    public byte[] exportActivitiesExcel() throws Exception {

        List<ActivityLog> activities = repo.findAll();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Activities");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Patient");

        header.createCell(1).setCellValue("Task");

        header.createCell(2).setCellValue("Status");

        header.createCell(3).setCellValue("Intern");

        int rowNum = 1;

        for (ActivityLog a : activities) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(a.getPatientName());

            row.createCell(1)
                    .setCellValue(a.getTask());

            row.createCell(2)
                    .setCellValue(a.getStatus());

            row.createCell(3)
                    .setCellValue(a.getInternEmail());
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return out.toByteArray();
    }
}
