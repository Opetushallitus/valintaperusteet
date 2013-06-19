-- poistetaan vanhat taulut ja luodaan ne uudelleen. helpompi näin kuin alkaa erikseen säätää constrainteja
drop table hakukohde_viite_opetuskielikoodi;
drop table hakukohde_viite_valintakoe;

alter table koodi rename to hakukohdekoodi;
delete from hakukohdekoodi where koodityyppi <> 'hakukohdekoodi';

alter table hakukohdekoodi drop column koodityyppi;

create table opetuskielikoodi (
    id int8 not null unique,
    version int8 not null,
    arvo varchar(255),
    nimi_en varchar(255),
    nimi_fi varchar(255),
    nimi_sv varchar(255),
    uri varchar(255) not null,
    valintaryhma_id int8,
    primary key (id)
);

create table valintakoekoodi (
    id int8 not null unique,
    version int8 not null,
    arvo varchar(255),
    nimi_en varchar(255),
    nimi_fi varchar(255),
    nimi_sv varchar(255),
    uri varchar(255) not null,
    valintaryhma_id int8,
    primary key (id)
);


create table hakukohde_viite_opetuskielikoodi (
    hakukohde_viite_id int8 not null,
    opetuskielikoodi_id int8 not null,
    primary key (hakukohde_viite_id, opetuskielikoodi_id),
    unique (hakukohde_viite_id, opetuskielikoodi_id)
);

create table hakukohde_viite_valintakoekoodi (
    hakukohde_viite_id int8 not null,
    valintakoekoodi_id int8 not null
);

alter table hakukohde_viite_opetuskielikoodi
    add constraint FKC4C6550FFB9CCDF9
    foreign key (opetuskielikoodi_id)
    references opetuskielikoodi;

alter table hakukohde_viite_opetuskielikoodi
    add constraint FKC4C6550FE4C115D4
    foreign key (hakukohde_viite_id)
    references hakukohde_viite;

alter table hakukohde_viite_valintakoekoodi
    add constraint FK9EA69D6D9521E41B
    foreign key (valintakoekoodi_id)
    references valintakoekoodi;

alter table hakukohde_viite_valintakoekoodi
    add constraint FK9EA69D6DE4C115D4
    foreign key (hakukohde_viite_id)
    references hakukohde_viite;

alter table opetuskielikoodi
    add constraint FKA60A60BA748BF879
    foreign key (valintaryhma_id)
    references valintaryhma;

alter table valintakoekoodi
    add constraint FKAE2CF062748BF879
    foreign key (valintaryhma_id)
    references valintaryhma;