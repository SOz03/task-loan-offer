import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API_SERVICES} from 'src/app/constants';
import {Credit} from 'src/app/models';
import {AuthService} from "../auth";
import {LoanOffer} from "../../models/loan-offer";

@Injectable({
  providedIn: 'root',
})
export class LoanOfferService {

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  public getAll(): Observable<any> {
    return this.http.get(API_SERVICES.loanOffer, {
      headers: this.authService.requestHeaders
    });
  }

  public get(id: string): Observable<any> {
    return this.http.get(`${API_SERVICES.loanOffer}/${id}`, {
      headers: this.authService.requestHeaders
    });
  }

  public create(loanOffer: LoanOffer): Observable<any> {
    return this.http.post(API_SERVICES.loanOffer, loanOffer, {
      headers: this.authService.requestHeaders
    });
  }

  public delete(id: string): Observable<any> {
    return this.http.delete(API_SERVICES.loanOffer + '/' + id, {
      headers: this.authService.requestHeaders
    });
  }
}
