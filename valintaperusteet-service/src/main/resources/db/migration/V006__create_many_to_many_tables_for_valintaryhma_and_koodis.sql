    alter table hakukohdekoodi drop column valintaryhma_id;
    alter table opetuskielikoodi drop column valintaryhma_id;
    alter table valintakoekoodi drop column valintaryhma_id;

    create table valintaryhma_hakukohdekoodi (
        valintaryhma_id int8 not null,
        hakukohdekoodi_id int8 not null,
        primary key (valintaryhma_id, hakukohdekoodi_id),
        unique (valintaryhma_id, hakukohdekoodi_id)
    );

    create table valintaryhma_opetuskielikoodi (
        valintaryhma_id int8 not null,
        opetuskielikoodi_id int8 not null,
        primary key (valintaryhma_id, opetuskielikoodi_id),
        unique (valintaryhma_id, opetuskielikoodi_id)
    );

    create table valintaryhma_valintakoekoodi (
        valintaryhma_id int8 not null,
        valintakoekoodi_id int8 not null
    );

    alter table valintaryhma_hakukohdekoodi
        add constraint FK9538B06B748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table valintaryhma_hakukohdekoodi
        add constraint FK9538B06BE47B5939
        foreign key (hakukohdekoodi_id)
        references hakukohdekoodi;

    alter table valintaryhma_opetuskielikoodi
        add constraint FKEC1C7817FB9CCDF9
        foreign key (opetuskielikoodi_id)
        references opetuskielikoodi;

    alter table valintaryhma_opetuskielikoodi
        add constraint FKEC1C7817748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table valintaryhma_valintakoekoodi
        add constraint FKE1FBF965748BF879
        foreign key (valintaryhma_id)
        references valintaryhma;

    alter table valintaryhma_valintakoekoodi
        add constraint FKE1FBF9659521E41B
        foreign key (valintakoekoodi_id)
        references valintakoekoodi;
