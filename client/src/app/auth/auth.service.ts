import {Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import * as jwt_decode from 'jwt-decode';
import {tap} from "rxjs/operators";
import {environment} from "../../environments/environment";

@Injectable()

export class AuthService {
  user = new Subject();
  private API_AUTH_URL = 'auth/login';
  private API_SIGN_UP_URL = 'api/public/signUp';

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string) {
    return this.http.post(environment.apiUrl + this.API_AUTH_URL, {
      email,
      password
    }, {responseType: 'text'}).pipe(tap(userData => {
      var claims: any = jwt_decode(userData);
      this.user.next(claims.sub);
      sessionStorage.setItem('username', claims.sub);
      sessionStorage.setItem('expirationDate', claims.exp);
      let tokenStr = 'Bearer ' + userData;
      sessionStorage.setItem('token', tokenStr);
    }));
  }

  signUp(email: string, password: string) {
    return this.http.post(environment.apiUrl + this.API_SIGN_UP_URL, {
      email,
      password
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
}
