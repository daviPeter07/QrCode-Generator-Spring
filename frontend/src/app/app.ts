import { Component, OnDestroy } from '@angular/core';
import { QrCode } from './services/qr-code';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnDestroy {
  url = '';
  isLoading = false;
  qrCodeImageUrl: string | null = null;
  errorMessage: string | null = null;

  //injetando o serviço de geração de QR Code
  constructor(private readonly qrCodeService: QrCode) {}

  private clearQrCodeImage(): void {
    if (this.qrCodeImageUrl) {
      URL.revokeObjectURL(this.qrCodeImageUrl);
      this.qrCodeImageUrl = null;
    }
  }
  generateQrCode(): void {
    //validacao se url esta vazia
    if (!this.url) {
      this.errorMessage = 'Por favor insira uma URL';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.clearQrCodeImage();

    this.qrCodeService.generateQrCode(this.url).subscribe({
      next: (imageBlob) => {
        this.qrCodeImageUrl = URL.createObjectURL(imageBlob);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao gerar o QR Code';
        this.isLoading = false;
      },
    });
  }

  ngOnDestroy(): void {
    this.clearQrCodeImage();
  }
}
