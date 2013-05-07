create table hakukohdekoodi (
    id int8 not null unique,
    version int8 not null,
    arvo varchar(255) not null,
    nimi_en varchar(255),
    nimi_fi varchar(255),
    nimi_sv varchar(255),
    uri varchar(255) not null,
    hakukohde_id int8,
    valintaryhma_id int8,
    primary key (id)
);

alter table hakukohdekoodi
    add constraint FK617F550748BF879
    foreign key (valintaryhma_id)
    references valintaryhma;

alter table hakukohdekoodi
    add constraint FK617F550CA6BF55C
    foreign key (hakukohde_id)
    references hakukohde_viite;