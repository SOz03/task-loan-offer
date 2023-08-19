create table data.tr_payment_schedules
(
    id            uuid
        constraint tr_payment_schedules_pk
            primary key default uuid_generate_v4(),
    created_at    bigint,
    updated_at    bigint,
    deleted_by    uuid  default null,
    date_payment  date        not null,
    amount        numeric(10, 2) not null,
    body          numeric(10, 2) not null,
    interest      numeric(10, 2) not null,
    loan_offer_id uuid          not null
);

comment on column data.tr_payment_schedules.date_payment is 'дата платежа';
comment on column data.tr_payment_schedules.amount is 'сумма платежа';
comment on column data.tr_payment_schedules.body is 'сумма гашения тела кредита';
comment on column data.tr_payment_schedules.interest is 'сумма гашения процентов';

create unique index tr_payment_schedules_id_uindex
    on data.tr_payment_schedules (id);

alter table data.tr_payment_schedules
    add constraint tr_payment_schedules_tr_loan_offer_fk
        foreign key (loan_offer_id) references data.tr_loan_offers;