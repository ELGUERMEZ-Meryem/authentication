import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import * as jwt_decode from 'jwt-decode';
import {map} from "rxjs/operators";
import {AuthService} from "./auth.service";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {
  isLoginMode = true;
  isFormSubmitted = false;
  loginForm: FormGroup;
  token: string = '';
  isLoading = false;
  error: null;
  isLoggedIn = false;

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  ngOnInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    });
    this.authService.user.subscribe(d => {
      this.isLoggedIn = !!d;
    });
    this.authService.autoLogin();
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
      this.http.post(`http://localhost:8080/auth/login`, {
        email: this.f().email.value,
        password: this.f().password.value
      }, {responseType: 'text'}).pipe(
        map(
          userData => {
            var claims: any = jwt_decode(userData);
            this.authService.user.next(claims.sub);
            sessionStorage.setItem('username', claims.sub);
            sessionStorage.setItem('expirationDate', claims.exp);
            let tokenStr = 'Bearer ' + userData;
            sessionStorage.setItem('token', tokenStr);
            return userData;
          }
        )
      ).subscribe(data => {
        this.token = data;
        this.error = null;
        this.isLoading = false;
        this.loginForm.reset();
      }, err => {
        this.isLoading = false;
        this.error = err;
      });
    } else {
      this.http.post(`http://localhost:8080/api/public/signUp`, {
        email: this.f().email.value,
        password: this.f().password.value
      }, {responseType: 'text'}).subscribe(data => {
        console.log('data ', data);
        this.token = data;
        this.isLoading = false;
        this.error = null;
        this.loginForm.reset();
      }, err => {
        this.isLoading = false;
        this.error = err;
      });
    }
  }

  signOut() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('expirationDate');
    sessionStorage.removeItem('token');
    this.authService.user.next(null);
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
}
