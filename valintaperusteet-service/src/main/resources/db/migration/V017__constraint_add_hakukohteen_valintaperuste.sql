alter table hakukohteen_valintaperuste
    add constraint FKDBD345BDE4C115D4
    foreign key (hakukohde_viite_id)
    references hakukohde_viite;