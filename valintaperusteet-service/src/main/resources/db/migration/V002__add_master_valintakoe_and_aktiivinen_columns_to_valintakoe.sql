alter table valintakoe add column aktiivinen boolean;
update valintakoe set aktiivinen = true;
alter table valintakoe alter column aktiivinen set not null;

alter table valintakoe add column master_valintakoe_id int8;

alter table valintakoe
    add constraint FKF7AE1AE57A46D9C
    foreign key (master_valintakoe_id)
    references valintakoe;