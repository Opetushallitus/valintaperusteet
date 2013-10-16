    drop table hakijaryhma_jono;
    drop table hakijaryhma;

    create table hakijaryhma (
        id int8 not null unique,
        version int8 not null,
        aktiivinen boolean not null,
        kiintio int4,
        kuvaus varchar(255),
        nimi varchar(255),
        oid varchar(255) not null unique,
        edellinen_hakijaryhma_id int8,
        hakukohde_viite_id int8,
        laskentakaava_id int8 not null,
        master_hakijaryhma_id int8,
        valintaryhma_id int8,
        primary key (id)
    );

    create table hakijaryhma_jono (
        jono_id int8 not null,
        hakijaryhma_id int8 not null,
        primary key (jono_id, hakijaryhma_id)
    );

    alter table hakijaryhma
        add constraint FK340BF1877C1201F8
        foreign key (master_hakijaryhma_id)
        references hakijaryhma;

    alter table hakijaryhma
        add constraint FK340BF187748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table hakijaryhma
        add constraint FK340BF187E4C115D4
        foreign key (hakukohde_viite_id)
        references hakukohde_viite;

    alter table hakijaryhma
        add constraint FK340BF1877D086ABB
        foreign key (laskentakaava_id)
        references laskentakaava;

    alter table hakijaryhma
        add constraint FK340BF187B3D288E6
        foreign key (edellinen_hakijaryhma_id)
        references hakijaryhma;

    alter table hakijaryhma_jono
        add constraint FKC68D817E1510C1AC
        foreign key (jono_id)
        references valintatapajono;

    alter table hakijaryhma_jono
        add constraint FKC68D817E46950E7B
        foreign key (hakijaryhma_id)
        references hakijaryhma;