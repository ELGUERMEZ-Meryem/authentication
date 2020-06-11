import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {catchError, finalize, tap} from "rxjs/operators";
import {AuthService} from "./auth.service";
import {throwError} from "rxjs";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {
  isLoginMode = true;
  isFormSubmitted = false;
  loginForm: FormGroup;
  isLoading = false;
  error = '';
  isLoggedIn = false;
  verify: boolean = false;
  userToSend: any;
  isEnabled2fa: boolean = false;
  isNotActivated: boolean = false;
  email: string;
  password: string;
  userToActivate: any;

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.initLoginForm();
    this.authService.user.subscribe(d => {
      this.isLoggedIn = !!d;
    });
    this.authService.autoLogin();
  }

  initLoginForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('a@a', [Validators.required, Validators.email]),
      password: new FormControl('111111', [Validators.required, Validators.minLength(6)]),
      phoneNumber: new FormControl('', [Validators.required]),
      enable2fa: new FormControl(false, []),
      type_2fa: new FormControl("GoogleAuth", [])
    });
  }

  onSwitchMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.isFormSubmitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.isLoading = true;
    if (this.isLoginMode) {
      this.logIn();
    } else {
      this.signUp();
    }
  }

  signOut() {
    this.authService.signOut();
  }

  /**
   * Checking control validation
   *
   * @param controlName: string => Equals to formControlName
   * @param validationType: string => Equals to valitors name
   */
  isControlHasError(controlName: string, validationType: string): boolean {
    const control = this.loginForm.controls[controlName];
    if (!control) {
      return false;
    }

    const result = control.hasError(validationType) && (control.dirty || control.touched || this.isFormSubmitted);
    return result;
  }

  isActivated(event: boolean) {
    this.isNotActivated = false;
  }

  private logIn() {
    this.authService.login(this.f().email.value, this.f().password.value).pipe(
      tap(
        data => {
          if (data['enabled2fa']) {
            this.isEnabled2fa = true;
            this.email = this.f().email.value;
            this.password = this.f().password.value;
          } else if (data['notEnabled']) {
            this.isNotActivated = true;
            this.userToActivate = {
              code_2fa: data['code_2fa'],
              email: this.f().email.value
            };
          }
          this.error = null;
          this.loginForm.reset();
        }
      ), catchError(err => {
        if (err.status === 401) {
          this.error = 'User name or password is incorrect';
        } else {
          this.error = 'A problem has occurred';
        }
        return throwError(err);
      }), finalize(() => this.isLoading = false)
    ).subscribe();
  }

  private signUp() {
    this.authService.signUp(this.f().email.value, this.f().password.value, this.f().phoneNumber.value, this.f().enable2fa.value, this.f().type_2fa.value)
      .pipe(tap(data => {
        this.error = '';
        this.loginForm.reset();
        if (data['is_2fa_enabled']) {
          this.verify = true;
          this.userToSend = data;
        }
      }), catchError(err => {
        if (err.status === 400) {
          console.log("jjjsjsjjs ", err)
          this.error = err.error.message;
        } else {
          this.error = 'A problem has occurred';
        }
        return throwError(err);
      }), finalize(() => this.isLoading = false)).subscribe();
  }
}
