
    create table arvokonvertteriparametri (
        id int8 not null unique,
        version int8 not null,
        hylkaysperuste boolean not null,
        paluuarvo varchar(255),
        arvo varchar(255),
        funktiokutsu_id int8 not null,
        primary key (id)
    );

    create table arvovalikonvertteriparametri (
        id int8 not null unique,
        version int8 not null,
        hylkaysperuste boolean not null,
        paluuarvo varchar(255),
        maxvalue float8 not null,
        minvalue float8 not null,
        palauta_haettu_arvo boolean,
        funktiokutsu_id int8 not null,
        primary key (id)
    );

    create table funktioargumentti (
        id int8 not null unique,
        version int8 not null,
        indeksi int4 not null,
        funktiokutsuchild_id int8,
        laskentakaavachild_id int8,
        funktiokutsuparent_id int8 not null,
        primary key (id)
    );

    create table funktiokutsu (
        id int8 not null unique,
        version int8 not null,
        funktionimi varchar(255) not null,
        primary key (id)
    );

    create table hakijaryhma (
        id int8 not null unique,
        version int8 not null,
        kasittelyjarjestys int4,
        nimi varchar(255),
        oid varchar(255) not null unique,
        on_poissulkeva boolean,
        valintaryhma_id int8,
        primary key (id)
    );

    create table hakijaryhma_jono (
        jono_id int8 not null,
        hakijaryhma_id int8 not null,
        primary key (jono_id, hakijaryhma_id)
    );

    create table hakukohde_viite (
        id int8 not null unique,
        version int8 not null,
        hakuoid varchar(255) not null,
        nimi varchar(255),
        oid varchar(255) not null unique,
        hakukohdekoodi_id int8,
        valintaryhma_id int8,
        primary key (id)
    );

    create table hakukohdekoodi (
        id int8 not null unique,
        version int8 not null,
        arvo varchar(255) not null,
        nimi_en varchar(255),
        nimi_fi varchar(255),
        nimi_sv varchar(255),
        uri varchar(255) not null,
        valintaryhma_id int8,
        primary key (id)
    );

    create table jarjestyskriteeri (
        id int8 not null unique,
        version int8 not null,
        aktiivinen boolean not null,
        metatiedot varchar(255),
        oid varchar(255) not null unique,
        edellinen_jarjestyskriteeri_id int8,
        laskentakaava_id int8 not null,
        master_jarjestyskriteeri_id int8,
        valintatapajono_id int8 not null,
        primary key (id)
    );

    create table laskentakaava (
        id int8 not null unique,
        version int8 not null,
        kuvaus varchar(255),
        nimi varchar(255) not null,
        on_luonnos boolean not null,
        tyyppi varchar(255) not null,
        funktiokutsu_id int8 not null,
        hakukohdeviite int8,
        valintaryhmaviite int8,
        primary key (id)
    );

    create table syoteparametri (
        id int8 not null unique,
        version int8 not null,
        arvo varchar(255) not null,
        avain varchar(255) not null,
        funktiokutsu_id int8 not null,
        primary key (id)
    );

    create table valinnan_vaihe (
        id int8 not null unique,
        version int8 not null,
        aktiivinen boolean not null,
        kuvaus varchar(255),
        nimi varchar(255) not null,
        oid varchar(255) not null unique,
        valinnan_vaihe_tyyppi varchar(255) not null,
        edellinen_valinnan_vaihe_id int8,
        hakukohde_viite_id int8,
        master_valinnan_vaihe_id int8,
        valintaryhma_id int8,
        primary key (id)
    );

    create table valintakoe (
        id int8 not null unique,
        version int8 not null,
        aktiivinen boolean not null,
        kuvaus varchar(255),
        nimi varchar(255) not null,
        oid varchar(255) not null unique,
        tunniste varchar(255) not null,
        laskentakaava_id int8,
        master_valintakoe_id int8,
        valinnan_vaihe_id int8 not null,
        primary key (id)
    );

    create table valintaperuste_viite (
        id int8 not null unique,
        version int8 not null,
        kuvaus varchar(255),
        lahde varchar(255) not null,
        on_paasykoe boolean not null,
        on_pakollinen boolean not null,
        tunniste varchar(255) not null,
        funktiokutsu_id int8 not null,
        primary key (id),
        unique (funktiokutsu_id)
    );

    create table valintaryhma (
        id int8 not null unique,
        version int8 not null,
        hakuOid varchar(255) not null,
        nimi varchar(255) not null,
        oid varchar(255) not null unique,
        parent_id int8,
        primary key (id)
    );

    create table valintatapajono (
        id int8 not null unique,
        version int8 not null,
        aktiivinen boolean not null,
        aloituspaikat int4 not null,
        kuvaus varchar(255),
        nimi varchar(255) not null,
        oid varchar(255) not null unique,
        siirretaan_sijoitteluun boolean not null,
        tasapistesaanto varchar(255) not null,
        edellinen_valintatapajono_id int8,
        master_valintatapajono_id int8,
        valinnan_vaihe_id int8 not null,
        primary key (id)
    );

    alter table arvokonvertteriparametri
        add constraint FKCD06F9A655CEAF9
        foreign key (funktiokutsu_id)
        references funktiokutsu;

    alter table arvovalikonvertteriparametri
        add constraint FK2F41E402655CEAF9
        foreign key (funktiokutsu_id)
        references funktiokutsu;

    alter table funktioargumentti
        add constraint FK589BC2C0AD95A88F
        foreign key (funktiokutsuparent_id)
        references funktiokutsu;

    alter table funktioargumentti
        add constraint FK589BC2C058D45459
        foreign key (funktiokutsuchild_id)
        references funktiokutsu;

    alter table funktioargumentti
        add constraint FK589BC2C0F8CB5365
        foreign key (laskentakaavachild_id)
        references laskentakaava;

    alter table hakijaryhma
        add constraint FK340BF187748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table hakijaryhma_jono
        add constraint FKC68D817E1510C1AC
        foreign key (jono_id)
        references valintatapajono;

    alter table hakijaryhma_jono
        add constraint FKC68D817E46950E7B
        foreign key (hakijaryhma_id)
        references hakijaryhma;

    alter table hakukohde_viite
        add constraint FKC9F42B4A748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table hakukohde_viite
        add constraint FKC9F42B4AE47B5939
        foreign key (hakukohdekoodi_id)
        references hakukohdekoodi;

    alter table hakukohdekoodi
        add constraint FK59BE65CE748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table jarjestyskriteeri
        add constraint FK331D107A91E6F3FB
        foreign key (valintatapajono_id)
        references valintatapajono;

    alter table jarjestyskriteeri
        add constraint FK331D107A55FB8A98
        foreign key (master_jarjestyskriteeri_id)
        references jarjestyskriteeri;

    alter table jarjestyskriteeri
        add constraint FK331D107A6ECAA706
        foreign key (edellinen_jarjestyskriteeri_id)
        references jarjestyskriteeri;

    alter table jarjestyskriteeri
        add constraint FK331D107A7D086ABB
        foreign key (laskentakaava_id)
        references laskentakaava;

    alter table laskentakaava
        add constraint FK4CB062B3B2564846
        foreign key (valintaryhmaviite)
        references valintaryhma;

    alter table laskentakaava
        add constraint FK4CB062B3655CEAF9
        foreign key (funktiokutsu_id)
        references funktiokutsu;

    alter table laskentakaava
        add constraint FK4CB062B3A9DCA529
        foreign key (hakukohdeviite)
        references hakukohde_viite;

    alter table syoteparametri
        add constraint FKD5AFAF79655CEAF9
        foreign key (funktiokutsu_id)
        references funktiokutsu;

    alter table valinnan_vaihe
        add constraint FK399E2731748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table valinnan_vaihe
        add constraint FK399E2731E4C115D4
        foreign key (hakukohde_viite_id)
        references hakukohde_viite;

    alter table valinnan_vaihe
        add constraint FK399E273152A39873
        foreign key (master_valinnan_vaihe_id)
        references valinnan_vaihe;

    alter table valinnan_vaihe
        add constraint FK399E2731403982C5
        foreign key (edellinen_valinnan_vaihe_id)
        references valinnan_vaihe;

    alter table valintakoe
        add constraint FKF7AE1AE7D086ABB
        foreign key (laskentakaava_id)
        references laskentakaava;

    alter table valintakoe
        add constraint FKF7AE1AE57A46D9C
        foreign key (master_valintakoe_id)
        references valintakoe;

    alter table valintakoe
        add constraint FKF7AE1AEDAF09910
        foreign key (valinnan_vaihe_id)
        references valinnan_vaihe;

    alter table valintaperuste_viite
        add constraint FK78BBB121655CEAF9
        foreign key (funktiokutsu_id)
        references funktiokutsu;

    alter table valintaryhma
        add constraint FK1CB07742546B0E11
        foreign key (parent_id)
        references valintaryhma;

    alter table valintatapajono
        add constraint FK82B665D71093E0F8
        foreign key (master_valintatapajono_id)
        references valintatapajono;

    alter table valintatapajono
        add constraint FK82B665D7D5BB40E6
        foreign key (edellinen_valintatapajono_id)
        references valintatapajono;

    alter table valintatapajono
        add constraint FK82B665D7DAF09910
        foreign key (valinnan_vaihe_id)
        references valinnan_vaihe;

    create sequence hibernate_sequence;