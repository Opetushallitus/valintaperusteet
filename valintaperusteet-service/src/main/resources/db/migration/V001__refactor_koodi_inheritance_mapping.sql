alter table hakukohdekoodi alter column arvo drop not null;
alter table hakukohdekoodi add column koodityyppi varchar(31);
update hakukohdekoodi set koodityyppi = 'hakukohdekoodi';
alter table hakukohdekoodi alter column koodityyppi set not null;
alter table hakukohdekoodi rename to koodi;