package com.davipeterson.qrcode.generator.services;

import com.davipeterson.qrcode.generator.dto.QrCodeGenerateResponse;
import com.davipeterson.qrcode.generator.ports.StoragePorts;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class QrCodeGeneratorService {
    private final StoragePorts storagePorts;

    public QrCodeGeneratorService(StoragePorts storagePorts) {
        this.storagePorts = storagePorts;
    }

    public QrCodeGenerateResponse generate(String text) throws WriterException, IOException {
        //cria o escritor pra gerar o qrcode
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        //gera matriz de pixel do qrcode a partir de texto
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        //cria o fluxo pra armazenar PNG
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        //gera o qrcode em memoria
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        //converte a imagem em
        byte[] pngQrCodeData = pngOutputStream.toByteArray();
        //faz upload e recebe da imagem e recebe url publica dela
        String url = storagePorts.uploadFile(pngQrCodeData, UUID.randomUUID().toString(), "image/png");

        return new QrCodeGenerateResponse(url);
    }

    public QrCodeGenerateResponse getLastGeneratedQrCode() {
        return new QrCodeGenerateResponse(storagePorts.getLastUploadedFileUrl());
    }
}
