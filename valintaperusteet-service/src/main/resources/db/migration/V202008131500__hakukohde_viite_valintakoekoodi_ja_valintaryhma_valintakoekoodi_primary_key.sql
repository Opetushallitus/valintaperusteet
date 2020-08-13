with rivi(hakukohde_viite_id, valintakoekoodi_id) as (
    delete from hakukohde_viite_valintakoekoodi
    returning hakukohde_viite_id, valintakoekoodi_id
)
insert into hakukohde_viite_valintakoekoodi (hakukohde_viite_id, valintakoekoodi_id)
select distinct hakukohde_viite_id, valintakoekoodi_id
from rivi;

alter table hakukohde_viite_valintakoekoodi
    add primary key (hakukohde_viite_id, valintakoekoodi_id);

alter table valintaryhma_valintakoekoodi
    add primary key (valintaryhma_id, valintakoekoodi_id);