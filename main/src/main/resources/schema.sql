create table if not exists users
(
    id    bigint generated by default as identity not null,
    name  varchar(255)                            not null,
    email varchar(512)                            not null,
    constraint pk_user primary key (id),
    constraint uq_user_email unique (email)
);

create table if not exists categories
(
    id   bigint generated by default as identity not null,
    name varchar(255)                            not null,
    constraint pk_category primary key (id),
    constraint uq_category_name unique (name)
);

create table if not exists locations
(
    id  bigint generated by default as identity not null,
    lat double precision                        not null,
    lon double precision                        not null,
    constraint pk_location primary key (id)
);

create table if not exists events
(
    id                 bigint generated by default as identity not null,
    annotation         varchar(1000)                           not null,
    category_id        bigint                                  not null references categories (id),
    created_on         timestamp without time zone             not null,
    description        varchar(2550)                           not null,
    event_date         timestamp without time zone             not null,
    initiator_id       bigint                                  not null references users (id),
    location_id        bigint                                  not null references locations (id),
    paid               boolean                                 not null,
    participant_limit  int,
    published_on       timestamp without time zone,
    request_moderation boolean,
    state              varchar(255),
    title              varchar(255),
    constraint pk_event primary key (id)
);

create table if not exists compilations
(
    id     bigint generated by default as identity not null,
    pinned boolean                                 not null,
    title  varchar(255)                            not null,
    constraint pk_compilation primary key (id)
);

create table if not exists compilations_events
(
    event_id       bigint not null references events (id),
    compilation_id bigint not null references compilations (id)
);

create table if not exists requests
(
    id           bigint generated by default as identity not null,
    created      timestamp without time zone             not null,
    event_id     bigint                                  not null references events (id),
    requester_id bigint                                  not null references users (id),
    status       varchar(25)                             not null,
    constraint pk_request primary key (id)
);

create table if not exists subscriptions
(
    id           bigint generated by default as identity not null,
    created      timestamp without time zone             not null,
    follower_id  bigint                                  not null references users (id),
    publisher_id bigint                                  not null references users (id),
    constraint pk_subscription primary key (id)
);