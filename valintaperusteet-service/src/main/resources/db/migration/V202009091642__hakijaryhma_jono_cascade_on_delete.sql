alter table hakijaryhma_jono
    drop constraint if exists "fk_4objpla3og2wm717mlrbq84xu",
    drop constraint if exists "fkc68d817e46950e7b",
    drop constraint if exists "fkc68d817e91e6f3fb",
    add foreign key (hakukohde_viite_id) references hakukohde_viite (id) on delete cascade,
    add foreign key (valintatapajono_id) references valintatapajono (id) on delete cascade,
    add foreign key (hakijaryhma_id) references hakijaryhma (id) on delete cascade;