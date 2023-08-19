create table data.tr_credits
(
    id         uuid
        constraint tr_credits_pk
            primary key default uuid_generate_v4(),
    created_at bigint,
    updated_at bigint,
    deleted_by uuid     default null,
    limitation numeric(9, 0) not null,
    rate       numeric(4, 2) not null,
    deadline   bigint        not null
);

create unique index tr_credits_id_uindex
    on data.tr_credits (id);

comment on column data.tr_credits.limitation is 'лимит по кредиту';
comment on column data.tr_credits.rate is 'процентная ставка';
comment on column data.tr_credits.deadline is 'максимальный срок кредита (кол-во в месяцах)';
