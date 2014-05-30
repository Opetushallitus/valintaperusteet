alter table valintaperuste_viite add column tekstiryhma_id int8;

alter table valintaperuste_viite add constraint FK78BBB1218C83865B foreign key (tekstiryhma_id) references tekstiryhma;






