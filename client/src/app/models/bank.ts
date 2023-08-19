import {Credit} from "./credit";
import {User} from "./user";

export class Bank {

  id: string = '';
  bankName: string = '';
  credits: Credit[] = [];
  users: User[] = [];
  show: boolean = true;

}
