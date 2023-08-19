import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_SERVICES } from 'src/app/constants';
import {AuthService} from "../auth";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient, private authService: AuthService) {
  }

  public getAll(): Observable<any> {
    return this.http.get(API_SERVICES.users, {
      headers: this.authService.requestHeaders
    });
  }
}
