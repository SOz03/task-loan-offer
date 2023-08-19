create table data.tr_loan_offers
(
    id          uuid
        constraint tr_loan_offers_pk
            primary key default uuid_generate_v4(),
    created_at  bigint,
    updated_at  bigint,
    deleted_by  uuid    default null,
    loan_amount numeric(7, 0) not null,
    user_id     uuid          not null,
    credit_id   uuid          not null,
    bank_id     uuid          not null
);

comment on column data.tr_loan_offers.loan_amount is 'сумма кредита';

create unique index tr_loan_offers_id_uindex
    on data.tr_loan_offers (id);

alter table data.tr_loan_offers
    add constraint tr_loan_offers_tr_users_id_fk
        foreign key (user_id) references data.tr_users;

alter table data.tr_loan_offers
    add constraint tr_loan_offers_tr_credits_id_fk
        foreign key (credit_id) references data.tr_credits;

alter table data.tr_loan_offers
    add constraint tr_loan_offers_tr_banks_id_fk
        foreign key (bank_id) references data.tr_banks;