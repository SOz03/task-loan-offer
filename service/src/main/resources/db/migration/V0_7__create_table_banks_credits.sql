create table data.tl_banks_credits
(
    id        uuid
        constraint tl_banks_credits_pk
            primary key default uuid_generate_v4(),
    bank_id   uuid not null,
    credit_id uuid not null
);

create unique index tl_banks_credits_id_uindex
    on data.tl_banks_credits (id);

alter table data.tl_banks_credits
    add constraint tl_banks_credits_tr_banks_id_fk
        foreign key (bank_id) references data.tr_banks;

alter table data.tl_banks_credits
    add constraint tl_banks_credits_tr_credits_id_fk
        foreign key (credit_id) references data.tr_credits;
