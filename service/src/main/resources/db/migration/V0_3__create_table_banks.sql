create table data.tr_banks
(
    id         uuid
        constraint tr_banks_pk
            primary key default uuid_generate_v4(),
    created_at bigint,
    updated_at bigint,
    deleted_by uuid     default null,
    bank_name       text not null
);

create unique index tr_banks_id_uindex
    on data.tr_banks (id);

create unique index tr_banks_name_uindex
    on data.tr_banks (bank_name);
