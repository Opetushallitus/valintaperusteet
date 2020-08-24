create table syotettavanarvonkoodi (
    id int8 not null unique,
    version int8 not null,
    arvo varchar(255),
    nimi_en varchar(255),
    nimi_fi varchar(255),
    nimi_sv varchar(255),
    uri varchar(255) not null,
    CONSTRAINT syotettavanarvonkoodi_pkey PRIMARY KEY (id),
    CONSTRAINT syotettavanarvonkoodi_uri_key UNIQUE (uri)
);

alter table valintaperuste_viite add column syotettavanarvontyyppi_id bigint;

alter table valintaperuste_viite
    add constraint FK_cfaf30f9e641e43a28bde4638e7c98ce
    foreign key (syotettavanarvontyyppi_id)
    references syotettavanarvonkoodi;

alter table valintaperuste_viite add column tilastoidaan boolean default false;

