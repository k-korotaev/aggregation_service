create table aggregation_service_tasks
(
    id         serial
        constraint aggregation_service_tasks_pk
            primary key,
    status     integer not null,
    error      varchar(255),
    type       varchar(30),
    created_at varchar(30),
    document   jsonb
);

alter table aggregation_service_tasks
    owner to spadmin;

create unique index aggregation_service_tasks_id_uindex
    on aggregation_service_tasks (id);