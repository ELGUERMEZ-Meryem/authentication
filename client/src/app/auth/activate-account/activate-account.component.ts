import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {catchError, finalize, tap} from "rxjs/operators";
import {throwError} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import * as qrcode from "qrcode-generator";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.css']
})
export class ActivateAccountComponent implements OnInit {

  verificationForm: FormGroup;
  qrSafeLink: SafeResourceUrl;
  qrCode: string;
  isLoading = false;
  @Input('user') user: any;
  @Output() isActivated = new EventEmitter<boolean>();
  private error: string;

  constructor(private authService: AuthService, private readonly sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.initVerificationForm();
    this.generateQRUrl(this.user.email, this.user.code_2fa);
  }

  initVerificationForm() {
    this.verificationForm = new FormGroup({
      code: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)])
    });
  }

  verifyCode() {
    if (this.verificationForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.authService.verifyCode(this.user.email, this.verificationForm.controls.code.value).pipe(tap(data => {
      //we should return to log in page by setting isNotActivated to false
      this.isActivated.emit(true);
      this.error = null;
    }), catchError(err => {
      if (err.status === 400) {
        this.error = err.error;
      } else {
        this.error = 'A problem has occurred';
      }
      return throwError(err);
    }), finalize(() => this.isLoading = false)).subscribe();
  }

  generateQRUrl(username: string, code: string) {
    //QR code represents a link with specific format, which can be scanned and understood by Google Authenicator.
    //To generate a QR image itself I used qrcode-generator
    const link = `otpauth://totp/${username}?secret=${code}&issuer=2fademo`;
    this.qrSafeLink = this.sanitizer.bypassSecurityTrustResourceUrl(link);
    const qrAdmin = qrcode(0, 'L');
    qrAdmin.addData(link);
    qrAdmin.make();
    this.qrCode = qrAdmin.createDataURL(4);
  }

  isControlHasError(controlName: string, validationType: string): boolean {
    const control = this.verificationForm.controls[controlName];
    if (!control) {
      return false;
    }

    const result = control.hasError(validationType) && (control.dirty || control.touched);
    return result;
  }

}
