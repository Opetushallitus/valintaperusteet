alter table hakijaryhma_jono add column hakijaryhmatyyppikoodi_id int8;
alter table hakijaryhma_jono add constraint hakijaryhma_jono_hakijaryhmatyyppikoodi_foreign_key
foreign key (hakijaryhmatyyppikoodi_id) references hakijaryhmatyyppikoodi;
