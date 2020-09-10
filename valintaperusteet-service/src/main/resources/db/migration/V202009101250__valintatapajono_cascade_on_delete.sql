alter table valintatapajono
    drop constraint if exists "fk82b665d7daf09910",
    add foreign key (valinnan_vaihe_id) references valinnan_vaihe (id) on delete cascade;