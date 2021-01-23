create table tournaments
(
    id               serial       not null
        constraint tournaments_pk
            primary key,
    name             varchar(100) not null,
    club_name        varchar(100),
    address          varchar(255),
    url              varchar(100),
    description      varchar(255),
    fee_info         varchar(255),
    free_form        boolean,
    with_handicap    boolean,
    category         varchar(32),
    max_participants integer,
    max_age          integer,
    min_age          integer,
    web_site         varchar(32)  not null,
    parsed_date      timestamp    not null,
    billiard_type    varchar(25),
    discipline       varchar(100),
    prize_fund_info  varchar(255),
    currency         varchar(10),
    is_free          boolean,
    constraint unique_url_name_website
        unique (url, name, web_site)
);

create index tournaments_web_site_url_index
    on tournaments (web_site, url, name);

create table events
(
    event_name    varchar(100) not null,
    date          timestamp    not null,
    tournament_id integer
        constraint events_tournaments_id_fk
            references tournaments
            on update restrict,
    id            serial       not null
        constraint events_pk
            primary key
);

create index events_tournament_id_idx
    on events (tournament_id);
