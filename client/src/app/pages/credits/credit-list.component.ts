import {Component, ComponentFactoryResolver, OnInit, ViewChild} from '@angular/core';
import {Credit} from '../../models';
import {AuthService, CreditService} from '../../services';
import {CreditModalWindow} from "../index";
import {RefDirective} from './ref.directive';

@Component({
  selector: 'app-credit-list',
  templateUrl: './credit-list.component.html',
  styleUrls: ['./credit-list.component.css'],
})
export class CreditListComponent implements OnInit {
  credits: Credit[] | undefined;
  searchText: string = '';
  contentSize: number = 0;
  showInfo = false;
  messageInfo = ''

  contentIsEmpty = false;

  @ViewChild(RefDirective, {static: false})
  refDir!: RefDirective;

  constructor(private creditService: CreditService,
              private authService: AuthService,
              private resolver: ComponentFactoryResolver) {
  }

  ngOnInit() {
    this.creditService.getAll().subscribe((data: Credit[]) => {
      this.credits = data;
      this.credits.forEach(el => el.show = true)
      this.contentSize = data.length;
    });
  }

  get hasEditAccess(): boolean {
    return this.authService.hasEditAccess;
  }

  delete(id: string) {
    this.creditService.delete(id).subscribe((response) => {
      console.log(response);
      if (this.credits !== undefined) {
        this.credits = this.credits.filter(item => {
          return item.id != id;
        });
      }
    });
    this.contentSize--
    this.messageInfo = 'Удаление успешно'
    this.showInfo = true;
    this.contentIsEmpty = (this.contentSize == 0)
  }

  closeInfo() {
    this.messageInfo = ''
    this.showInfo = false;
  }

  search(val: string) {
    let size: number = 0
    this.credits?.forEach(el => {
      if (el.limitation.toString().indexOf(val) == -1) {
        el.show = false
      } else {
        size++
        el.show = true
      }
    })
    this.contentSize = size;
    this.contentIsEmpty = (size == 0)
  }

  edit(credit: Credit) {
    const modalFactory = this.resolver.resolveComponentFactory(CreditModalWindow)
    this.refDir.containerRef.clear()

    const component = this.refDir.containerRef.createComponent(modalFactory)
    component.instance.header = 'Изменение записи'
    component.instance.credit = credit
    component.instance.close.subscribe(() => {
      this.refDir.containerRef.clear()
    })
  }

  addCredit() {
    const modalFactory = this.resolver.resolveComponentFactory(CreditModalWindow)
    this.refDir.containerRef.clear()

    const component = this.refDir.containerRef.createComponent(modalFactory)
    component.instance.header = 'Новая запись';
    component.instance.credit = new Credit();
    component.instance.close.subscribe(() => {
      this.credits?.push(component.instance.credit)
      component.instance.credit = new Credit()
      this.refDir.containerRef.clear()
      window.location.reload();
    })
  }
}
