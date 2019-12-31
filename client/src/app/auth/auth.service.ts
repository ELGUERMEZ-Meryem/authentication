import {Subject} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  token: string = '';
  user = new Subject();
 constructor() { }

  autoLogin(){
    const username = sessionStorage.getItem('username');
    let expirationDate = new Date(sessionStorage.getItem('expirationDate'));
    const token = sessionStorage.getItem('token');
    if(!username){
      return;
    }
    // if(!expirationDate || expirationDate < new Date()) {
    //   return;
    // }
    this.user.next(username);
  }
}
