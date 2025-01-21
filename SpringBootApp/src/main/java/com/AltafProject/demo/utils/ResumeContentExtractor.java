package com.AltafProject.demo.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;


@Component
public class ResumeContentExtractor {


    private String extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public String extractContent(MultipartFile file) throws IOException {
        // Save MultipartFile to a temporary file
        File tempFile = File.createTempFile("resume", ".pdf");
        file.transferTo(tempFile);

        // Extract text using Apache PDFBox
        String textContent = extractTextFromPdf(tempFile);

        // Delete temporary file
        tempFile.delete();
        //System.out.println(textContent);
        return textContent;
    }



}
