package com.davipeterson.qrcode.generator.controller;

import com.davipeterson.qrcode.generator.dto.QrCodeGenerateRequest;
import com.davipeterson.qrcode.generator.dto.QrCodeGenerateResponse;
import com.davipeterson.qrcode.generator.services.QrCodeGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {

    private final QrCodeGeneratorService service;

    public QrCodeController(QrCodeGeneratorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<QrCodeGenerateResponse> generate(@RequestBody QrCodeGenerateRequest request) {
        try {
            QrCodeGenerateResponse response = service.generate(request.text());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
