import { Component } from '@angular/core';
import { AuthService } from './services';
import { Router } from '@angular/router';
import { User } from './models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'template-page';

  user: User | null = null;

  constructor(private authService: AuthService, private router: Router) {
    this.user = this.authService.user;
  }

  get isAuthenticated(): boolean {
    return this.authService.isLoggedIn;
  }


  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  get hasEditAccess(): boolean {
    return this.authService.hasEditAccess;
  }
}
