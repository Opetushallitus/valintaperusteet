create table lokalisoitu_teksti (
	id int8 not null unique,
	version int8 not null,
	kieli varchar(255) not null,
	teksti varchar(255) not null,
	tekstiryhma_id int8 not null,
	primary key (id)
);

create table tekstiryhma (
    id int8 not null unique,
    version int8 not null,
    primary key (id)
);


alter table arvokonvertteriparametri add column tekstiryhma_id int8;

alter table arvokonvertteriparametri add constraint FKCD06F9A8C83865B foreign key (tekstiryhma_id) references tekstiryhma;

alter table lokalisoitu_teksti add constraint FK46E400BD27A0ADDE foreign key (tekstiryhma_id) references tekstiryhma;





