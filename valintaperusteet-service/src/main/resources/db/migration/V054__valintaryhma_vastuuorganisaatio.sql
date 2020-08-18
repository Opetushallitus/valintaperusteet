alter table valintaryhma add column vastuuorganisaatio_id int8;
alter table valintaryhma add constraint valintaryhma_vastuuorganisaatio_foreign_key
foreign key (vastuuorganisaatio_id) references organisaatio;
