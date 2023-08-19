import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, StorageService } from 'src/app/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form: FormGroup;
  isLoginFailed = false;
  showAlert = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router
  ) {
    this.form = new FormGroup({
      username: new FormControl('', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(20),
      ]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(4),
      ]),
    });
  }

  ngOnInit(): void {}

  closeAlert() {
    this.showAlert = false;
  }

  onSubmit(event: Event): void {
    event.preventDefault();

    const { username, password } = this.form.value;

    console.log(this.form.value);

    this.authService.login({ username, password }).subscribe({
      next: (data) => {
        if (
          data.statusCode === 'OK'
        ) {
          const user = data.body;

          this.storageService.saveToken(user.token);
          this.storageService.saveUser(user);
          this.authService.setUser(user);
        }
      },
      complete: () => {
        this.isLoginFailed = false;
        this.router.navigate(['/home']).then(r =>{

        });
      },
      error: (err) => {
        this.errorMessage = err.message || '';
        this.showAlert = true;
        this.isLoginFailed = true;
      },
    });
  }

  reloadPage(): void {
    window.location.reload();
  }
}
