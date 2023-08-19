import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API_SERVICES} from 'src/app/constants';
import {Credit} from 'src/app/models';
import {AuthService} from "../auth";

@Injectable({
  providedIn: 'root',
})
export class CreditService {
  constructor(private http: HttpClient, private authService: AuthService) {
  }

  public getAll(): Observable<any> {
    return this.http.get(API_SERVICES.credit, {
      headers: this.authService.requestHeaders
    });
  }

  public get(id: string): Observable<any> {
    return this.http.get(`${API_SERVICES.credit}/${id}`, {
      headers: this.authService.requestHeaders
    });
  }

  public create(credit: Credit): Observable<any> {
    return this.http.post(API_SERVICES.credit, credit, {
      headers: this.authService.requestHeaders
    });
  }

  public update(credit: any): Observable<any> {
    return this.http.put(`${API_SERVICES.credit}/${credit.id}`, credit, {
      headers: this.authService.requestHeaders
    });
  }

  public delete(id: string): Observable<any> {
    return this.http.delete(API_SERVICES.credit + '/' + id, {
      headers: this.authService.requestHeaders
    });
  }
}
