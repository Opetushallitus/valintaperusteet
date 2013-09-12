    create table hakukohteen_valintaperuste (
        id int8 not null unique,
        version int8 not null,
        arvo varchar(255) not null,
        kuvaus varchar(255),
        tunniste varchar(255) not null,
        hakukohde_viite_id int8 not null,
        primary key (id),
        unique (tunniste, hakukohde_viite_id)
    );