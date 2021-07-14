alter table valinnan_vaihe
    drop constraint if exists "fk399e2731748bf879",
    drop constraint if exists "fk399e2731e4c115d4",
    add foreign key (valintaryhma_id) references valintaryhma (id) on delete cascade,
    add foreign key (hakukohde_viite_id) references hakukohde_viite (id) on delete cascade;