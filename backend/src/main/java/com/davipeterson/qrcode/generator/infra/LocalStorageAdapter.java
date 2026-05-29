package com.davipeterson.qrcode.generator.infra;

import com.davipeterson.qrcode.generator.ports.StoragePorts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Component
@Profile("!s3")
public class LocalStorageAdapter implements StoragePorts {

    private final Path storageDir;

    public LocalStorageAdapter(@Value("${app.storage.local.dir}") String storageDir) throws IOException {
        this.storageDir = Paths.get(storageDir);
        Files.createDirectories(this.storageDir);
    }

    @Override
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        try {
            Files.write(storageDir.resolve(fileName + ".png"), fileData);
            return "/qrcodes/" + fileName + ".png";
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar QR code localmente", e);
        }
    }

    @Override
    public String getLastUploadedFileUrl() {
        try (Stream<Path> files = Files.list(storageDir)) {
            Path lastFile = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".png"))
                    .max(Comparator.comparing(this::getLastModifiedTime))
                    .orElseThrow(() -> new NoSuchElementException("Nenhum QR code encontrado"));

            return "/qrcodes/" + lastFile.getFileName();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar ultimo QR code local", e);
        }
    }

    private long getLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler data do arquivo", e);
        }
    }
}
