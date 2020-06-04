import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AuthComponent} from './auth/auth.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {AuthService} from "./auth/auth.service";
import { Verify2faSecretCodeComponent } from './auth/verify2fa-secret-code/verify2fa-secret-code.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    Verify2faSecretCodeComponent
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

