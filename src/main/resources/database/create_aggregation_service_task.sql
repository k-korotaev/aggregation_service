create table aggregation_service_tasks
(
    id         serial
        constraint aggregation_service_tasks_pk
            primary key,
    status     integer           not null,
    error      varchar(1000),
    type       numeric,
    created_at timestamp         not null,
    document   jsonb,
    request    jsonb             not null,
    version    integer default 0 not null
);

alter table aggregation_service_tasks
    owner to spadmin;

create unique index aggregation_service_tasks_id_uindex
    on aggregation_service_tasks (id);