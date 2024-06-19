alter table hakukohde_viite add column if not exists last_modified timestamptz default now();
alter table hakukohde_viite add column if not exists transaction_id bigint not null default txid_current();

alter table hakukohteen_valintaperuste add column if not exists last_modified timestamptz default now();
alter table hakukohteen_valintaperuste add column if not exists transaction_id bigint not null default txid_current();

alter table valintaperuste_viite add column if not exists last_modified timestamptz default now();
alter table valintaperuste_viite add column if not exists transaction_id bigint not null default txid_current();

alter table syotettavanarvonkoodi add column if not exists last_modified timestamptz default now();
alter table syotettavanarvonkoodi add column if not exists transaction_id bigint not null default txid_current();

alter table valinnan_vaihe add column if not exists last_modified timestamptz default now();
alter table valinnan_vaihe add column if not exists transaction_id bigint not null default txid_current();

alter table valintatapajono add column if not exists last_modified timestamptz default now();
alter table valintatapajono add column if not exists transaction_id bigint not null default txid_current();

alter table jarjestyskriteeri add column if not exists last_modified timestamptz default now();
alter table jarjestyskriteeri add column if not exists transaction_id bigint not null default txid_current();

alter table laskentakaava add column if not exists last_modified timestamptz default now();
alter table laskentakaava add column if not exists transaction_id bigint not null default txid_current();

alter table funktiokutsu add column if not exists last_modified timestamptz default now();
alter table funktiokutsu add column if not exists transaction_id bigint not null default txid_current();

alter table funktioargumentti add column if not exists last_modified timestamptz default now();
alter table funktioargumentti add column if not exists transaction_id bigint not null default txid_current();

alter table syoteparametri add column if not exists last_modified timestamptz default now();
alter table syoteparametri add column if not exists transaction_id bigint not null default txid_current();

alter table arvokonvertteriparametri add column if not exists last_modified timestamptz default now();
alter table arvokonvertteriparametri add column if not exists transaction_id bigint not null default txid_current();

alter table arvovalikonvertteriparametri add column if not exists last_modified timestamptz default now();
alter table arvovalikonvertteriparametri add column if not exists transaction_id bigint not null default txid_current();

alter table tekstiryhma add column if not exists last_modified timestamptz default now();
alter table tekstiryhma add column if not exists transaction_id bigint not null default txid_current();

alter table lokalisoitu_teksti add column if not exists last_modified timestamptz default now();
alter table lokalisoitu_teksti add column if not exists transaction_id bigint not null default txid_current();

alter table valintakoe add column if not exists last_modified timestamptz default now();
alter table valintakoe add column if not exists transaction_id bigint not null default txid_current();

-- Luodaan funktio joka hoitaa aikaleiman ja transaction_id:n päivityksen
create or replace function set_values_on_insert_or_update() returns trigger as
$$
begin
    new.last_modified := now()::timestamptz;
    new.transaction_id := txid_current();
return new;
end;
$$ language plpgsql;


create trigger on_hakukohde_viite_insert_or_update
    before insert or update
    on hakukohde_viite
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_hakukohteen_valintaperuste_insert_or_update
    before insert or update
    on hakukohteen_valintaperuste
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_valintaperuste_viite_insert_or_update
    before insert or update
    on valintaperuste_viite
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_syotettavanarvonkoodi_insert_or_update
    before insert or update
    on syotettavanarvonkoodi
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_valinnan_vaihe_insert_or_update
    before insert or update
    on valinnan_vaihe
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_valintatapajono_insert_or_update
    before insert or update
    on valintatapajono
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_jarjestyskriteeri_insert_or_update
    before insert or update
    on jarjestyskriteeri
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_laskentakaava_insert_or_update
    before insert or update
    on laskentakaava
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_funktiokutsu_insert_or_update
    before insert or update
    on funktiokutsu
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_funktioargumentti_insert_or_update
    before insert or update
    on funktioargumentti
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_syoteparametri_insert_or_update
    before insert or update
    on syoteparametri
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_arvokonvertteriparametri_insert_or_update
    before insert or update
    on arvokonvertteriparametri
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_arvovalikonvertteriparametri_insert_or_update
    before insert or update
    on arvovalikonvertteriparametri
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_tekstiryhma_insert_or_update
    before insert or update
    on tekstiryhma
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_lokalisoitu_teksti_insert_or_update
    before insert or update
    on lokalisoitu_teksti
    for each row
    execute procedure set_values_on_insert_or_update();

create trigger on_valintakoe_insert_or_update
    before insert or update
    on valintakoe
    for each row
    execute procedure set_values_on_insert_or_update();
