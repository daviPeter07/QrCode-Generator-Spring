import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

interface GenerateQrCodeRequest {
  text: string;
}
//decorador para indicar que essa classe é um serviço injetável e pode ser fornecida em toda a aplicação
@Injectable({
  providedIn: 'root',
})
export class QrCode {
  //variavel, private pq so pode ser acessada dentro da classe, readonly pq n pode ser alterada depois de inicializada
  private readonly apiUrl = 'http://localhost:8080/api/qrcode';

  constructor(private readonly http: HttpClient) {}

  generateQrCode(text: string) {
    try {
      const requestBody: GenerateQrCodeRequest = { text };
  
      return this.http.post(this.apiUrl, requestBody, { responseType: 'blob' });
    } catch (error) {
      console.error('Erro ao gerar QR Code:', error);
      throw error;
    }
  }
}
