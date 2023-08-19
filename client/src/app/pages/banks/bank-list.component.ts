import {Component, OnInit} from '@angular/core';
import {Bank, Credit, Response} from 'src/app/models';
import {AuthService, BankService, CreditService} from 'src/app/services';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-bank-list',
  templateUrl: './bank-list.component.html',
  styleUrls: ['./bank-list.component.css'],
})
export class BankListComponent implements OnInit {
  changeBank = new Bank();
  updateBankCredits !: Credit[]
  banksForm!: FormGroup
  creditsForm!: FormGroup

  itemCredit!: Credit;

  banks: Bank[] = [];
  allCredits: Credit[] = [];

  isUpdate = false;
  isCreate = true;

  searchText: string = '';
  contentIsEmpty = false;

  private setActive: string = 'fade show active'
  private setOff: string = ''

  tabInfo: string = this.setActive
  tabSave: string = this.setOff

  constructor(private bankService: BankService,
              private creditService: CreditService,
              private authService: AuthService) {
  }

  ngOnInit() {
    this.bankService.getAll().subscribe((data?: Bank[]) => {
      this.banks = data || [];
      this.banks.forEach(el => el.show = true)
      this.contentIsEmpty = (this.banks.length == 0)
    });

    this.creditService.getAll().subscribe((credits?: Credit[]) => {
      this.allCredits = credits || [];
    })

    this.banksForm = new FormGroup({
      bank: new FormControl(Bank)
    });

    this.creditsForm = new FormGroup({
      credits: new FormControl(Credit)
    });
  }

  create() {
    this.bankService.create(this.changeBank).subscribe({
      next: (data: Response) => {
        if (data.errorMessage == null) {
          console.log(data.content)
          data.content.forEach(el => {
            el.show = true
            this.banks.push(el)
          })
        }
      },
      complete: () => {

      },
      error: (err) => {
        console.log(err)
      },
    });
    this.changeBank = new Bank()
  }

  update() {
    this.bankService.update(this.changeBank).subscribe({
      next: (data) => {
        if (data.statusCode === 'OK') {
          console.log(data)
        }
      },
      complete: () => {

      },
      error: (err) => {
        console.log(err)
      },
    });
    this.changeBank = new Bank()
  }

  delete(bank: Bank) {
    this.bankService.delete(bank.id).subscribe((response) => {
      console.log(response);
      if (this.banks !== undefined) {
        this.banks = this.banks.filter(item => {
          return item.id != bank.id;
        });
      }
    });
  }

  removeCredit(credit: Credit) {
    if (this.changeBank.credits !== undefined) {
      this.changeBank.credits = this.changeBank.credits.filter(item => {
        return item.id != credit.id;
      });
    }
  }

  selectBank() {
    this.changeBank = this.banksForm.value.bank
  }

  selectCredit() {
    this.itemCredit = this.creditsForm.value.credits

    console.log(this.itemCredit)
    let count = 0;
    if (this.itemCredit != null && this.changeBank.credits !== undefined) {
      this.changeBank.credits.forEach(item => {
        if (item.id == this.itemCredit.id) {
          count++;
        }
      });
      if (count == 0) {
        this.changeBank.credits.push(this.itemCredit)
      }
    }
    console.log(this.changeBank.credits)
    console.log(count)
  }

  stateUserInBank(bankId: string) {
    let isMyBank = false;
    this.banks?.forEach(bank => {
      if (bank.id == bankId && bank.users.length != 0) {
        isMyBank = true;
      }
    })
    return isMyBank;
  }

  search(val: string) {
    let size: number = 0
    this.banks?.forEach(el => {
      if (el.bankName.toLowerCase().indexOf(val.toLowerCase()) == -1) {
        el.show = false
      } else {
        el.show = true
        size++
      }
    })
    this.contentIsEmpty = (size == 0)
  }

  get hasEditAccess(): boolean {
    return this.authService.hasEditAccess;
  }

  setUpdate(isUpdate: any) {
    this.isUpdate = isUpdate;
    this.isCreate = isUpdate == false;
  }

  clickTag(numTag: number) {
    if (numTag == 1) {
      this.tabInfo = this.setActive
      this.tabSave = this.setOff
    }
    if (numTag == 2) {
      this.tabInfo = this.setOff
      this.tabSave = this.setActive
    }
  }

}
