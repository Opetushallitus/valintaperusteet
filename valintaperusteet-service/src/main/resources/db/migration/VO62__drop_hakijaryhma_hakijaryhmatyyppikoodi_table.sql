drop table hakijaryhma_hakijaryhmatyyppikoodi;

alter table hakijaryhma add column hakijaryhmatyyppikoodi_id int8;
alter table hakijaryhma add constraint hakijaryhma_hakijaryhmatyyppikoodi_foreign_key
foreign key (hakijaryhmatyyppikoodi_id) references hakijaryhmatyyppikoodi;
