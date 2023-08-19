import {Component, OnInit} from '@angular/core';
import {AuthService, BankService, CreditService, UserService} from 'src/app/services';
import {LoanOfferService} from "../../services/loan-offer";
import {Bank, Credit, LoanOffer, PaymentSchedule, Response, User} from "../../models";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-loan-offer',
  templateUrl: './loan-offer.component.html',
  styleUrls: ['./loan-offer.component.css'],
})
export class LoanOfferComponent implements OnInit {

  currentBank!: Bank

  currentLoanOffer: LoanOffer | undefined;
  currentPayments: PaymentSchedule[] | undefined;

  loanOffers: LoanOffer[] | undefined;
  banks: Bank[] | undefined;
  users: User[] | undefined;

  loanOfferForm!: FormGroup

  searchText: string = '';
  contentIsEmpty = false;
  currentUser: any;

  constructor(private authService: AuthService,
              private loanOfferService: LoanOfferService,
              private bankService: BankService,
              private creditService: CreditService,
              private userService: UserService) {

  }
  ngOnInit(): void {
    this.currentUser = this.authService.user;
    this.clickTag(1)

    this.loanOfferService.getAll().subscribe((data?: LoanOffer[]) => {
      console.log(data)
      this.loanOffers = data || [];
      this.loanOffers.forEach(el => el.show = true)
      this.contentIsEmpty = (this.loanOffers.length == 0)
    });

    this.userService.getAll().subscribe((users?: User[]) => {
      console.log(users)
      this.users = users
    });

    this.bankService.getAll().subscribe((banks?: Bank[]) => {
      console.log(banks)
      this.banks = banks
    });

    this.loanOfferForm = new FormGroup({
      bank: new FormControl(Bank),
      user: new FormControl(User),
      credit: new FormControl(Credit)
    });

  }

  setCurrentBank($event: Event){
    this.currentBank = this.loanOfferForm.get("bank")?.value
  }


  create() {
    let entity = new LoanOffer()
    entity.bank = this.loanOfferForm.value.bank
    entity.user = this.loanOfferForm.value.user
    entity.credit = this.loanOfferForm.value.credit

    this.loanOfferService.create(entity).subscribe({
      next: (data: Response) => {
        if (data.errorMessage == null && this.loanOffers != undefined) {
          console.log(data.content)
          data.content.forEach(el => {
            el.show = true
            // this.loanOffers.push(el)
          })
        }
      },
      complete: () => {

      },
      error: (err) => {
        console.log(err)
      },
    });
  }



  tabAll: string = 'fade show active'
  tabPayment: string = ''
  private setActive: string = 'fade show active'
  private setOff: string = ''

  clickTag(numTag: number) {
    if (numTag == 1) {
      this.tabAll = this.setActive
      this.tabPayment = this.setOff
    }
    if (numTag == 2) {
      this.tabAll = this.setOff
      this.tabPayment = this.setActive
    }
  }

  printGraph(el: LoanOffer) {
    this.currentLoanOffer = el;
    el.printGraph = !el.printGraph;
    el.printVal = el.printGraph ? 'show' : '';

    this.loanOffers?.forEach(val => {
      if (val.id == el.id) {
        this.currentPayments = val.paymentSchedules
      }
    })
  }

  searchUser(val: string) {
    let size: number = 0
    this.loanOffers?.forEach(el => {
      if (el.user?.fullname.toLowerCase().indexOf(val.toLowerCase()) == -1) {
        el.show = false
      } else {
        el.show = true
        size++
      }
    })
    this.contentIsEmpty = (size == 0)
  }

  delete(loanOffer: LoanOffer) {
    this.loanOfferService.delete(loanOffer.id).subscribe((response) => {
      console.log(response);
      if (this.loanOffers !== undefined) {
        this.loanOffers = this.loanOffers.filter(item => {
          return item.id != loanOffer.id;
        });
      }
    });
  }

  get hasEditAccess(): boolean {
    return this.authService.hasEditAccess;
  }
}
