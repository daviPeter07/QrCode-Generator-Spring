package com.davipeterson.qrcode.generator.dto;

import jakarta.validation.constraints.NotBlank;

public record QrCodeGenerateRequest(
        @NotBlank(message = "O texto/URL nao pode estar vazio")
        String text
) {
}
