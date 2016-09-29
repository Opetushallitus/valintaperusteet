create table hakijaryhmatyyppikoodi (
    id int8 not null unique,
    version int8 not null,
    arvo varchar(255),
    nimi_en varchar(255),
    nimi_fi varchar(255),
    nimi_sv varchar(255),
    uri varchar(255) not null,
    CONSTRAINT hakijaryhmatyyppikoodi_pkey PRIMARY KEY (id),
    CONSTRAINT vhakijaryhmatyyppikoodi_uri_key UNIQUE (uri)
);


create table hakijaryhma_hakijaryhmatyyppikoodi (
    hakijaryhma_id int8 not null,
    hakijaryhmatyyppikoodi_id int8 not null,
    CONSTRAINT hakijaryhma_hakijaryhmatyyppikoodi_pkey PRIMARY KEY (hakijaryhma_id, hakijaryhmatyyppikoodi_id)
);

alter table hakijaryhma_hakijaryhmatyyppikoodi
    add constraint FK_b918b7567820bc35e578b657
    foreign key (hakijaryhma_id)
    references hakijaryhma;

alter table hakijaryhma_hakijaryhmatyyppikoodi
    add constraint FK_2a481e87ae24dd2eb553ce0e6
    foreign key (hakijaryhmatyyppikoodi_id)
    references hakijaryhmatyyppikoodi;
