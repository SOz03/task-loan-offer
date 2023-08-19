import {Credit} from "./credit";
import {User} from "./user";
import {Bank} from "./bank";
import {PaymentSchedule} from "./payment-schedule";

export class LoanOffer {

  id: string = '';
  loanAmount: number | undefined;
  bank: Bank | undefined;
  credit: Credit | undefined;
  user: User | undefined;
  paymentSchedules: PaymentSchedule[] | undefined;

  show: boolean = true;

  printGraph: boolean = false;
  printVal: string = '';
}
