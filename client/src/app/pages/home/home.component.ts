import {Component, OnInit} from '@angular/core';
import {AuthService} from 'src/app/services';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  currentUser: any;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.currentUser = this.authService.user;
  }
}
