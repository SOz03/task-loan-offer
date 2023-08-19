import { Injectable } from '@angular/core';
import { STORAGE_TOKEN_KEY, STORAGE_USER_KEY } from 'src/app/constants';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  storage = localStorage;

  constructor() {}

  clear(): void {
    this.storage.clear();
  }

  public saveToken(token: string): void {
    this.storage.removeItem(STORAGE_TOKEN_KEY);
    this.storage.setItem(STORAGE_TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return this.storage.getItem(STORAGE_TOKEN_KEY);
  }

  public saveUser(user: any): void {
    this.storage.removeItem(STORAGE_USER_KEY);
    this.storage.setItem(STORAGE_USER_KEY, JSON.stringify(user));
  }

  public getUser(): any {
    const user = this.storage.getItem(STORAGE_USER_KEY);
    return user ? JSON.parse(user) : {};
  }
}
