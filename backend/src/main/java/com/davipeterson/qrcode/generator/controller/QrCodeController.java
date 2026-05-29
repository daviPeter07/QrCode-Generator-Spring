package com.davipeterson.qrcode.generator.controller;

import com.davipeterson.qrcode.generator.dto.QrCodeGenerateRequest;
import com.davipeterson.qrcode.generator.dto.QrCodeGenerateResponse;
import com.davipeterson.qrcode.generator.services.QrCodeGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {

    private final QrCodeGeneratorService service;

    public QrCodeController(QrCodeGeneratorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<QrCodeGenerateResponse> generate(@Valid @RequestBody QrCodeGenerateRequest request) {
        try {
            QrCodeGenerateResponse response = service.generate(request.text());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<QrCodeGenerateResponse> getQrCode() {
        try {
            QrCodeGenerateResponse response = service.getLastGeneratedQrCode();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
