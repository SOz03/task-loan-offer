import {LoanOffer} from "./loan-offer";
import {LocalDate} from 'ts-extended-types';

export class PaymentSchedule {

  id: string = '';
  datePayment: LocalDate | undefined;
  amount: number | undefined;
  body: number | undefined;
  interest: number | undefined;
  loanOffer: LoanOffer | undefined;

  show: boolean = true;

}
