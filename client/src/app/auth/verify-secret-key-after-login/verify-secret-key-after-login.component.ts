import {Component, Input, OnInit} from '@angular/core';
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
      console.log('authentication with success  ');
    }), catchError(err => {
      if (err.status === 401) {
        this.error = 'User name or password is incorrect';
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
