import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Credit} from 'src/app/models/credit';
import {Response} from 'src/app/models/response';
import {CreditService} from "../../services";

@Component({
  selector: 'app-modal-window',
  templateUrl: './credit-modal-window.component.html',
  styleUrls: ['./credit-modal-window.component.css'],
})
export class CreditModalWindow implements OnInit {

  credit!: Credit;

  showAlert = false;
  errorMessage: string | undefined;
  levelMessage: string = 'alert-success';

  constructor(private creditService: CreditService) {
  }

  ngOnInit() {
  }

  closeAlert() {
    this.showAlert = false;
  }

  saveCredit(credit: Credit) {
    if (credit.id == '') {
      this.creditService.create(this.credit).subscribe((response) => {
        const res: Response = response;
        if (res.errorMessage != undefined) {
          this.levelMessage = 'alert-danger'
          this.errorMessage = response.errorMessage;
        } else {
          this.errorMessage = 'Запись добавлена';
        }
        this.showAlert = true;
        this.credit = response.content.get(0)
      });
    } else {
      this.creditService.update(this.credit).subscribe((response) => {
        const res: Response = response;
        if (res.errorMessage != undefined) {
          this.levelMessage = 'alert-danger'
          this.errorMessage = response.errorMessage;
        } else {
          this.errorMessage = 'Запись изменена';
        }
        this.showAlert = true;
      });
    }

  }

  @Input() header: string = ""
  @Output() close = new EventEmitter<void>()
}
