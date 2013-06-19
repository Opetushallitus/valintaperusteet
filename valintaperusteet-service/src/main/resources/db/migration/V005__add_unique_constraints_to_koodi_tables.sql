begin;

update hakukohde_viite set hakukohdekoodi_id = null where hakukohdekoodi_id=2;
delete from hakukohdekoodi where uri = 'hakukohteet_490';

--update valintaryhma set opetuskielikoodi_id = null where opetuskielikoodi_id = 23;
delete from hakukohde_viite_opetuskielikoodi  where opetuskielikoodi_id = 23;
delete from opetuskielikoodi where uri = 'kieli_fi';

alter table hakukohdekoodi add unique (uri);
alter table opetuskielikoodi add unique (uri);
alter table valintakoekoodi add unique (uri);

commit;