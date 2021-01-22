alter table jarjestyskriteeri
    drop constraint if exists "fk331d107a91e6f3fb",
    add foreign key (valintatapajono_id) references valintatapajono (id) on delete cascade;
