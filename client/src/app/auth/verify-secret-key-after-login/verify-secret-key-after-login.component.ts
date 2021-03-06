import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../auth.service";
import {catchError, finalize, tap} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'app-verify-secret-key-after-login',
  templateUrl: './verify-secret-key-after-login.component.html',
  styleUrls: ['./verify-secret-key-after-login.component.css']
})
export class VerifySecretKeyAfterLoginComponent implements OnInit {

  verificationForm: FormGroup;
  isLoading = false;
  error = '';
  @Input('password') password: any;
  @Input('email') email: any;
  @Output() isActivated = new EventEmitter<boolean>();

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.initVerificationForm();
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
    this.authService.loginWithVerification(this.email, this.password, this.verificationForm.controls.code.value).pipe(tap(data => {
      //we should return to log in page by setting isSMSSanded and isEnabled2fa to false
      this.isActivated.emit(true);
      console.log('authentication with success  ');
    }), catchError(err => {
      if (err.status === 401) {
        this.error = 'The verification code is not correct!';
      } else {
        this.error = 'A problem has occurred';
      }      return throwError(err);
    }), finalize(() => this.isLoading = false)).subscribe();
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
