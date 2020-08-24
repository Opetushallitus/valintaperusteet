alter table laskentakaava add column kopio_laskentakaavasta_id BIGINT;

alter table laskentakaava
    add constraint kopio_laskentakaavasta_reference
    foreign key (kopio_laskentakaavasta_id)
    references laskentakaava;
