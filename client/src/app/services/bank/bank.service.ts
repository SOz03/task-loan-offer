import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API_SERVICES} from 'src/app/constants';
import {Bank} from 'src/app/models';
import {AuthService} from "../auth";

@Injectable({providedIn: 'root',})
export class BankService {
  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  public getAll(): Observable<any> {
    return this.http.get(API_SERVICES.bank,{
      headers: this.authService.requestHeaders
    });
  }

  public get(id: string): Observable<any> {
    return this.http.get(`${API_SERVICES.bank}/${id}`,{
      headers: this.authService.requestHeaders
    });
  }

  public create(bank: Bank): Observable<any> {
    return this.http.post(API_SERVICES.bank, bank, {
      headers: this.authService.requestHeaders
    });
  }

  public update(bank: any): Observable<any> {
    return this.http.put(`${API_SERVICES.bank}/${bank.id}`, bank, {
      headers: this.authService.requestHeaders
    });
  }

  public delete(id: string): Observable<any> {
    return this.http.delete(`${API_SERVICES.bank}/${id}`,{
      headers: this.authService.requestHeaders
    });
  }
}
