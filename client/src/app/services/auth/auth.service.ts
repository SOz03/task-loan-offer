import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API_SERVICES, PREFIX_TOKEN, STORAGE_USER_KEY} from 'src/app/constants';
import {LoginParams, RegisterParams} from './types';
import {StorageService} from '..';
import {User} from 'src/app/models';
import {business} from 'src/app/types';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'}),
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  user: User | null = null;

  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) {
    try {
      const storageData = window.localStorage.getItem(STORAGE_USER_KEY);

      if (storageData) {
        this.user = JSON.parse(storageData);
      }
    } catch (e) {
      console.log(e);
    }
  }

  get requestHeaders(): any {
    const token = this.storageService.getToken()
    if (token) {
      return {
        AUTHORIZATION: PREFIX_TOKEN + token
      }
    }

    return {}
  }

  get isLoggedIn(): boolean {
    return Boolean(this.user);
  }

  get hasEditAccess(): boolean {
    return this.user?.role === business.UserRole.admin;
  }

  setUser = (user: User) => {
    this.user = user;
  };

  login = (params: LoginParams): Observable<any> => {
    return this.http.post(`${API_SERVICES.login}`, params, httpOptions);
  };

  register = (params: RegisterParams): Observable<any> => {
    return this.http.post(
      `${API_SERVICES.registration}`,
      params,
      httpOptions
    );
  };

  logout = () => {
    this.user = null;
    this.storageService.clear();
  };
}
