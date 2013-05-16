alter table hakukohde_viite add column hakukohdekoodi_id int8;
alter table hakukohde_viite add constraint FKC9F42B4AE47B5939 foreign key (hakukohdekoodi_id) references hakukohdekoodi;
alter table hakukohdekoodi drop column hakukohde_id;