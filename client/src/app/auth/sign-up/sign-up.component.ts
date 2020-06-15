import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../auth.service";
import {catchError, finalize, tap} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {
  signUpForm: FormGroup;
  isLoading = false;
  error = '';
  isLoggedIn = false;
  verify: boolean = false;
  userToSend: any;

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
    this.signUpForm = new FormGroup({
      email: new FormControl('a@a', [Validators.required, Validators.email]),
      password: new FormControl('111111', [Validators.required, Validators.minLength(6)]),
      phoneNumber: new FormControl('', [Validators.required]),
      enable2fa: new FormControl(false, []),
      type_2fa: new FormControl("GoogleAuth", [])
    });
  }

  f() {
    return this.signUpForm.controls;
  }

  onSubmit() {
    if (this.signUpForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.signUp();
  }

  /**
   * Checking control validation
   *
   * @param controlName: string => Equals to formControlName
   * @param validationType: string => Equals to valitors name
   */
  isControlHasError(controlName: string, validationType: string): boolean {
    const control = this.signUpForm.controls[controlName];
    if (!control) {
      return false;
    }

    const result = control.hasError(validationType) && (control.dirty || control.touched);
    return result;
  }

  isActivated(event: boolean) {
    this.verify = false;
  }

  private signUp() {
    let type_2fa = this.f().type_2fa.value;
    if(!this.f().enable2fa.value){
      type_2fa = null;
    }
    this.authService.signUp(this.f().email.value, this.f().password.value, this.f().phoneNumber.value, this.f().enable2fa.value, type_2fa)
      .pipe(tap(data => {
        this.error = '';
        this.signUpForm.reset();
        if (data['is_2fa_enabled'] && data['code_2fa'] != null) {
          this.verify = true;
          this.userToSend = data;
        }
      }), catchError(err => {
        if (err.status === 400) {
          this.error = err.error.message;
        } else {
          this.error = 'A problem has occurred';
        }
        return throwError(err);
      }), finalize(() => this.isLoading = false)).subscribe();
  }
}
