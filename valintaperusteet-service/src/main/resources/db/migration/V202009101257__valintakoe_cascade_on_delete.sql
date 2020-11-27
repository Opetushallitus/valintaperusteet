alter table valintakoe
    drop constraint if exists "fkf7ae1aedaf09910",
    add foreign key (valinnan_vaihe_id) references valinnan_vaihe (id) on delete cascade;