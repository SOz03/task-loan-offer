import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  canActivate(): boolean | Promise<boolean> {
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/login']);
    }
    return true;
  }
}
