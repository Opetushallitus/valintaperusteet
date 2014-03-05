alter table arvovalikonvertteriparametri add column hylkaysperuste varchar(255) not NULL DEFAULT 'false';

alter table arvovalikonvertteriparametri add column tekstiryhma_id int8;

alter table arvovalikonvertteriparametri add constraint FK2F41E4028C83865B foreign key (tekstiryhma_id) references tekstiryhma;






