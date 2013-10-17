    alter table valintaperuste_viite add column indeksi int4;
    update valintaperuste_viite set indeksi = 1;

    alter table valintaperuste_viite alter column indeksi set not null;
    alter table valintaperuste_viite drop constraint valintaperuste_viite_funktiokutsu_id_key;
    alter table hakukohteen_valintaperuste
        add constraint FKDBD345BDE4C115D4
        foreign key (hakukohde_viite_id)
        references hakukohde_viite;

    alter table valintaperuste_viite add unique (funktiokutsu_id, indeksi);