alter table hakijaryhma
    drop constraint if exists "fk340bf187748bf879",
    add foreign key (valintaryhma_id) references valintaryhma (id) on delete cascade;