# QR Code Generator

API REST para geração de QR codes com armazenamento em disco local ou MinIO (S3-compatible).

## Arquitetura

```
[Client] ── POST /api/qrcode ──→ QrCodeController
                                       │
                                QrCodeGeneratorService
                                       │
                                StoragePorts (interface)
                                       │
                          ┌────────────┴────────────┐
                    LocalStorageAdapter    S3StorageAdapter
                    (profile default)      (profile s3)
                          │                     │
                    ./qrcodes/              MinIO
                    (disco local)      (bucket S3 local)
```

### Stack

- Java 21 + Spring Boot 4.0.6
- Google ZXing 3.5.4 (geração dos QR codes)
- AWS SDK S3 2.24.12 (compatível com MinIO)
- Maven wrapper

## Como rodar

### Com Docker (app + MinIO)

```bash
docker compose up -d --build
```

| Serviço | Acesso |
|---|---|
| API | http://localhost:8080 |
| Console MinIO | http://localhost:9001 |

### Local (sem Docker, salva em disco)

```bash
.\mvnw.cmd spring-boot:run
```

A API fica em http://localhost:8080. Os QR codes são salvos em `./qrcodes/` e servidos como conteúdo estático.

## Uso da API

```bash
curl -X POST http://localhost:8080/api/qrcode \
  -H "Content-Type: application/json" \
  -d '{"text": "https://github.com"}'
```

Resposta:

```json
{
  "url": "http://localhost:9000/qrcodes/550e8400-e29b-41d4-a716-446655440000.png"
}
```

## Perfis Spring

| Perfil | Storage | Descrição |
|---|---|---|
| `default` | Disco local (`./qrcodes/`) | Modo dev, sem dependências |
| `s3` | MinIO / S3 | Para usar com Docker ou AWS S3 de verdade |

Para ativar o profile S3:

```bash
docker compose up -d               # via Docker (já configurado)
# ou
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=s3
```

## Variáveis de ambiente

Configure no `.env` (para Docker) ou exporte diretamente:

| Variável | Default | Descrição |
|---|---|---|
| `AWS_ENDPOINT` | `http://minio:9000` | Endpoint do S3/MinIO |
| `AWS_REGION` | `us-east-1` | Região AWS |
| `AWS_S3_BUCKET_NAME` | `qrcodes` | Nome do bucket |
| `AWS_S3_PUBLIC_URL` | `http://localhost:9000` | URL pública para acessar as imagens |
| `AWS_ACCESS_KEY_ID` | `minioadmin` | Access key |
| `AWS_SECRET_ACCESS_KEY` | `minioadmin` | Secret key |

## Estrutura do projeto

```
src/
└── main/java/com/davipeterson/qrcode/generator/
    ├── Application.java              # Entry point Spring Boot
    ├── controller/QrCodeController.java
    ├── dto/
    │   ├── QrCodeGenerateRequest.java
    │   └── QrCodeGenerateResponse.java
    ├── ports/StoragePorts.java       # Interface (port)
    ├── services/QrCodeGeneratorService.java
    └── infra/
        ├── LocalStorageAdapter.java  # Storage em disco (default)
        └── S3StorageAdapter.java     # Storage S3/MinIO (profile s3)
```

## Comandos úteis

```bash
docker compose up -d --build      # build + sobe tudo
docker compose down -v             # derruba tudo (remove volume)
docker compose logs -f             # logs em tempo real
docker compose logs app            # logs só do backend
```
