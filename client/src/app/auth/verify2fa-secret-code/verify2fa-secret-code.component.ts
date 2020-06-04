import {Component, Input, OnInit} from '@angular/core';
import {catchError, tap} from "rxjs/operators";
import {throwError} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import * as qrcode from "qrcode-generator";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-verify2fa-secret-code',
  templateUrl: './verify2fa-secret-code.component.html',
  styleUrls: ['./verify2fa-secret-code.component.css']
})
export class Verify2faSecretCodeComponent implements OnInit {

  verificationForm: FormGroup;
  qrSafeLink: SafeResourceUrl;
  qrCode: string;
  @Input('user') user: any;

  constructor(private authService: AuthService, private readonly sanitizer: DomSanitizer) { }

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
    this.authService.verifyCode(this.user, this.verificationForm.controls.code.value).pipe(tap(data => {
      console.log('data ');
    }), catchError(err => {
      console.log('error ');
      return throwError(err);
    })).subscribe();
  }

  generateQRUrl(username: string, code: string) {
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