# QR Code Generator

Monorepo com backend Spring Boot e frontend Angular para geração de QR codes com armazenamento em disco local ou MinIO (S3-compatible).

## Arquitetura

```
[Angular] ── POST /api/qrcode ──→ Spring Boot API
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

**Backend:**
- Java 21 + Spring Boot 4.0.6
- Google ZXing 3.5.4 (geração dos QR codes)
- AWS SDK S3 2.24.12 (compatível com MinIO)
- springdoc-openapi 3.0.3 (documentação automática)
- Jakarta Validation (validação com @NotBlank)

**Frontend:**
- Angular (em estruturação)

## Como rodar

### Com Docker (backend + MinIO)

```bash
docker compose up -d --build
```

| Serviço | Acesso |
|---|---|
| Backend API | http://localhost:8080 |
| Console MinIO | http://localhost:9001 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### Backend local (sem Docker, salva em disco)

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

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

Requisições com `text` vazio retornam erro 400.

## Perfis Spring

| Perfil | Storage | Descrição |
|---|---|---|
| `default` | Disco local (`./qrcodes/`) | Modo dev, sem dependências |
| `s3` | MinIO / S3 | Para usar com Docker ou AWS S3 de verdade |

Para ativar o profile S3:

```bash
docker compose up -d               # via Docker (já configurado)
# ou
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=s3
```

## Documentação da API

Com o backend rodando, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Variáveis de ambiente

Configure no `.env` (raiz do monorepo) ou exporte diretamente:

| Variável | Default | Descrição |
|---|---|---|
| `AWS_ENDPOINT` | `http://minio:9000` | Endpoint do S3/MinIO |
| `AWS_REGION` | `us-east-1` | Região AWS |
| `AWS_S3_BUCKET_NAME` | `qrcodes` | Nome do bucket |
| `AWS_S3_PUBLIC_URL` | `http://localhost:9000` | URL pública para acessar as imagens |
| `AWS_ACCESS_KEY_ID` | `minioadmin` | Access key |
| `AWS_SECRET_ACCESS_KEY` | `minioadmin` | Secret key |

## Estrutura do projeto (monorepo)

```
qrcode.generator/
├── backend/                        # Spring Boot API
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/.../
│       ├── Application.java
│       ├── config/OpenApiConfig.java
│       ├── controller/QrCodeController.java
│       ├── dto/
│       ├── ports/StoragePorts.java
│       ├── services/QrCodeGeneratorService.java
│       └── infra/
│           ├── LocalStorageAdapter.java
│           └── S3StorageAdapter.java
├── frontend/                       # Angular (em estruturação)
├── docker-compose.yml
├── .env
└── README.md
```

## Comandos úteis

```bash
docker compose up -d --build      # build + sobe tudo
docker compose down -v             # derruba tudo (remove volume)
docker compose logs -f             # logs em tempo real
docker compose logs app            # logs só do backend
```
