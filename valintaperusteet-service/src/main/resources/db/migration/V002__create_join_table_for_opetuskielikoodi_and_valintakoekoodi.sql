    create table hakukohde_viite_opetuskielikoodi (
        hakukohde_viite_id int8 not null,
        opetuskielikoodi_id int8 not null,
        primary key (hakukohde_viite_id, opetuskielikoodi_id)
    );

    create table hakukohde_viite_valintakoe (
        hakukohde_viite_id int8 not null,
        valintakoe_id int8 not null,
        primary key (hakukohde_viite_id, valintakoe_id)
    );

    alter table hakukohde_viite_opetuskielikoodi
        add constraint FKC4C6550FFB9CCDF9
        foreign key (opetuskielikoodi_id)
        references koodi;

    alter table hakukohde_viite_opetuskielikoodi
        add constraint FKC4C6550FE4C115D4
        foreign key (hakukohde_viite_id)
        references hakukohde_viite;

    alter table hakukohde_viite_valintakoe
        add constraint FK9605F9C3264DF4F
        foreign key (valintakoe_id)
        references koodi;

    alter table hakukohde_viite_valintakoe
        add constraint FK9605F9C3E4C115D4
        foreign key (hakukohde_viite_id)
        references hakukohde_viite;


