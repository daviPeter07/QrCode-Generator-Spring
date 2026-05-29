package com.davipeterson.qrcode.generator.ports;

public interface StoragePorts {
    String uploadFile(byte[] fileData, String fileName, String contentType);
    String getLastUploadedFileUrl();
}
