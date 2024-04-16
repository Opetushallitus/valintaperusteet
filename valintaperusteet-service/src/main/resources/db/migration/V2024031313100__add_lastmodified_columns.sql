alter table hakukohde_viite
    add column if not exists last_modified timestamptz;

alter table hakukohteen_valintaperuste
    add column if not exists last_modified timestamptz;

alter table valintaperuste_viite
    add column if not exists last_modified timestamptz;

alter table syotettavanarvonkoodi
    add column if not exists last_modified timestamptz;

alter table valinnan_vaihe
    add column if not exists last_modified timestamptz;

alter table valintatapajono
    add column if not exists last_modified timestamptz;

alter table jarjestyskriteeri
    add column if not exists last_modified timestamptz;

alter table laskentakaava
    add column if not exists last_modified timestamptz;

alter table funktiokutsu
    add column if not exists last_modified timestamptz;

alter table funktioargumentti
    add column if not exists last_modified timestamptz;

alter table syoteparametri
    add column if not exists last_modified timestamptz;

alter table arvokonvertteriparametri
    add column if not exists last_modified timestamptz;

alter table arvovalikonvertteriparametri
    add column if not exists last_modified timestamptz;

alter table tekstiryhma
    add column if not exists last_modified timestamptz;

alter table lokalisoitu_teksti
    add column if not exists last_modified timestamptz;

alter table valintakoe
    add column if not exists last_modified timestamptz;

-- Luodaan funktio joka hoitaa aikaleiman päivityksen
create or replace function set_last_modified() returns trigger as
$$
begin
    new.last_modified := now()::timestamptz;
return new;
end;
$$ language plpgsql;


create or replace trigger set_hakukohde_viite_last_modified_on_change
    before insert or update
    on hakukohde_viite
    for each row
    execute procedure set_last_modified();

create or replace trigger set_hakukohteen_valintaperuste_last_modified_on_change
    before insert or update
    on hakukohteen_valintaperuste
    for each row
    execute procedure set_last_modified();

create or replace trigger set_valintaperuste_viite_last_modified_on_change
    before insert or update
    on valintaperuste_viite
    for each row
    execute procedure set_last_modified();

create or replace trigger set_syotettavanarvonkoodi_last_modified_on_change
    before insert or update
    on syotettavanarvonkoodi
    for each row
    execute procedure set_last_modified();

create or replace trigger set_valinnan_vaihe_last_modified_on_change
    before insert or update
    on valinnan_vaihe
    for each row
    execute procedure set_last_modified();

create or replace trigger set_valintatapajono_last_modified_on_change
    before insert or update
    on valintatapajono
    for each row
    execute procedure set_last_modified();

create or replace trigger set_jarjestyskriteeri_last_modified_on_change
    before insert or update
    on jarjestyskriteeri
    for each row
    execute procedure set_last_modified();

create or replace trigger set_laskentakaava_last_modified_on_change
    before insert or update
    on laskentakaava
    for each row
    execute procedure set_last_modified();

create or replace trigger set_funktiokutsu_last_modified_on_change
    before insert or update
    on funktiokutsu
    for each row
    execute procedure set_last_modified();

create or replace trigger set_funktioargumentti_last_modified_on_change
    before insert or update
    on funktioargumentti
    for each row
    execute procedure set_last_modified();

create or replace trigger set_syoteparametri_last_modified_on_change
    before insert or update
    on syoteparametri
    for each row
    execute procedure set_last_modified();

create or replace trigger set_arvokonvertteriparametri_last_modified_on_change
    before insert or update
    on arvokonvertteriparametri
    for each row
    execute procedure set_last_modified();

create or replace trigger set_arvovalikonvertteriparametri_last_modified_on_change
    before insert or update
    on arvovalikonvertteriparametri
    for each row
    execute procedure set_last_modified();

create or replace trigger set_tekstiryhma_last_modified_on_change
    before insert or update
    on tekstiryhma
    for each row
    execute procedure set_last_modified();

create or replace trigger set_lokalisoitu_teksti_last_modified_on_change
    before insert or update
    on lokalisoitu_teksti
    for each row
    execute procedure set_last_modified();

create or replace trigger set_valintakoe_last_modified_on_change
    before insert or update
    on valintakoe
    for each row
    execute procedure set_last_modified();
