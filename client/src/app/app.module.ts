import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppComponent} from './app.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppRoutingModule} from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {
  BankListPage,
  CreditModalWindow,
  CreditListPage,
  HomePage,
  LoginPage,
  ProfilePage,
  RegistrationPage,
  LoanOfferPage
} from './pages';
import { RefDirective } from './pages/credits/ref.directive';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
  ],
  declarations: [
    AppComponent,
    RegistrationPage,
    LoginPage,
    ProfilePage,
    BankListPage,
    CreditListPage,
    CreditModalWindow,
    HomePage,
    LoanOfferPage,
    RefDirective
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
