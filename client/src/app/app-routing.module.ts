import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard, HomeGuard } from './guards';
import {
  LoginPage,
  RegistrationPage,
  BankListPage,
  ProfilePage,
  CreditListPage,
  LoanOfferPage,
  HomePage
} from './pages';

const appRoutes: Routes = [
  {
    path: 'login',
    component: LoginPage,
    canActivate: [HomeGuard],
  },
  {
    path: 'registration',
    component: RegistrationPage,
    canActivate: [HomeGuard],
  },
  {
    path: 'banks',
    component: BankListPage,
    canActivate: [AuthGuard],
  },
  {
    path: 'credits',
    component: CreditListPage,
    canActivate: [AuthGuard],
  },
  {
    path: 'loan-offer',
    component: LoanOfferPage,
    canActivate: [AuthGuard],
  },
  {
    path: 'profile',
    component: ProfilePage,
    canActivate: [AuthGuard],
  },
  {
    path: 'home',
    component: HomePage,
  },
  {
    path: '#',
    redirectTo: 'home',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes)],
  exports: [RouterModule],
  providers: [AuthGuard, HomeGuard],
})
export class AppRoutingModule {}
