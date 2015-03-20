alter table valintaperuste_viite add column syotettavissa_kaikille boolean default true;
update valintaperuste_viite set syotettavissa_kaikille = NOT vaatii_osallistumisen;
