create table organisaatio (
  id int8 not null unique,
  version int8 not null,
  oid varchar(255) not null unique,
  primary key (id)
);

create table valintaryhma_organisaatio (
  valintaryhma_id int8 not null,
  organisaatio_id int8 not null,
  primary key (valintaryhma_id, organisaatio_id)
);

alter table valintaryhma_organisaatio
add constraint FK1220D43C748BF879
foreign key (valintaryhma_id)
references valintaryhma;

alter table valintaryhma_organisaatio
add constraint FK1220D43C5D2AC759
foreign key (organisaatio_id)
references organisaatio;
