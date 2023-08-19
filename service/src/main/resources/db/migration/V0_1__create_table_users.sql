create extension if not exists "uuid-ossp";

create table data.tr_users
(
    id               uuid
        constraint tr_users_pk
            primary key default uuid_generate_v4(),
    created_at       bigint,
    updated_at       bigint,
    deleted_by       uuid default null,
    username         text not null,
    encrypt_password text not null,
    fullname         text,
    email            text,
    phone            text,
    city             text,
    role             text not null
);

create unique index tr_users_id_uindex
    on data.tr_users (id);

create unique index tr_users_username_uindex
    on data.tr_users (username);

create unique index tr_users_mail_uindex
    on data.tr_users (email);

