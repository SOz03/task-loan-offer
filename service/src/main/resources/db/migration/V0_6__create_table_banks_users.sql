create table data.tl_banks_users
(
    id      uuid
        constraint tl_banks_users_pk
            primary key default uuid_generate_v4(),
    bank_id uuid not null,
    user_id uuid not null
);

create unique index tl_banks_users_id_uindex
    on data.tl_banks_users (id);

alter table data.tl_banks_users
    add constraint tl_banks_users_tr_banks_id_fk
        foreign key (bank_id) references data.tr_banks;

alter table data.tl_banks_users
    add constraint tl_banks_users_tr_users_id_fk
        foreign key (user_id) references data.tr_users;
