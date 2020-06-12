import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AuthComponent} from './auth/auth.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {AuthService} from "./auth/auth.service";
import { ActivateAccountComponent } from './auth/activate-account/activate-account.component';
import { VerifySecretKeyAfterLoginComponent } from './auth/verify-secret-key-after-login/verify-secret-key-after-login.component';
import {SignUpComponent} from "./auth/sign-up/sign-up.component";

@NgModule({
    declarations: [
        AppComponent,
        AuthComponent,
        ActivateAccountComponent,
        VerifySecretKeyAfterLoginComponent,
        SignUpComponent
    ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule {
}

