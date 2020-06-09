import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import * as jwt_decode from 'jwt-decode';
import {map, tap} from "rxjs/operators";
import {environment} from "../../environments/environment";

@Injectable()
export class AuthService {
  user = new Subject();
  private API_AUTH_URL = 'auth/login';
  private API_SIGN_UP_URL = 'api/public/signUp';
  private API_VERIFY_CODE_URL = 'api/public/verificationCode';

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string) {
    return this.http.post(environment.apiUrl + this.API_AUTH_URL, {
      email,
      password
    }).pipe(map(userData => {
      if ((userData['enabled2fa'] != null && userData['enabled2fa']) || ( userData['notEnabled'] != null && userData['notEnabled'])) {
        return userData;
      }
      var claims: any = jwt_decode(userData);
      this.user.next(claims.sub);
      sessionStorage.setItem('username', claims.sub);
      sessionStorage.setItem('expirationDate', claims.exp);
      let tokenStr = 'Bearer ' + userData;
      sessionStorage.setItem('token', tokenStr);
      return userData;
    }));
  }

  signUp(email: string, password: string, is_2fa_enabled: boolean) {
    return this.http.post(environment.apiUrl + this.API_SIGN_UP_URL, {
      email,
      password,
      is_2fa_enabled
    }, {responseType: 'text'});
  }

  verifyCode(email: string, code_2fa: string) {
    return this.http.post(environment.apiUrl + this.API_VERIFY_CODE_URL, {
      email,
      code_2fa
    }, {responseType: 'text'});
  }

  autoLogin() {
    const username = sessionStorage.getItem('username');
    let expirationDate = new Date(+sessionStorage.getItem('expirationDate') * 1000);
    if (!username) {
      return;
    }
    if (expirationDate < new Date()) {
      return;
    }
    this.user.next(username);
  }

  signOut() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('expirationDate');
    sessionStorage.removeItem('token');
    this.user.next(null);
  }

  loginWithVerification(email: any, password: any, code_2fa: any) {
    return this.http.post(environment.apiUrl + this.API_AUTH_URL, {
      email,
      password,
      code_2fa
    }).pipe(tap(userData => {
      var claims: any = jwt_decode(userData);
      this.user.next(claims.sub);
      sessionStorage.setItem('username', claims.sub);
      sessionStorage.setItem('expirationDate', claims.exp);
      let tokenStr = 'Bearer ' + userData;
      sessionStorage.setItem('token', tokenStr);
    }));
  }
}
