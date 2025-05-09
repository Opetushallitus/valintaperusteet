alter table hakukohde_viite_history add column if not exists update_time timestamptz;
create index if not exists hakukohde_viite_history_update_time_idx ON hakukohde_viite_history (update_time);
update hakukohde_viite_history set update_time = upper(system_time) where system_time != 'empty';
update hakukohde_viite_history set update_time = now() where system_time = 'empty';
alter table hakukohde_viite_history drop column if exists system_time;
alter table hakukohde_viite_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table arvokonvertteriparametri_history add column if not exists update_time timestamptz;
create index if not exists arvokonvertteriparametri_history_update_time_idx ON arvokonvertteriparametri_history (update_time);
update arvokonvertteriparametri_history set update_time = upper(system_time) where system_time != 'empty';
update arvokonvertteriparametri_history set update_time = now() where system_time = 'empty';
alter table arvokonvertteriparametri_history drop column if exists system_time;
alter table arvokonvertteriparametri_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table arvovalikonvertteriparametri_history add column if not exists update_time timestamptz;
create index if not exists arvovalikonvertteriparametri_history_update_time_idx ON arvovalikonvertteriparametri_history (update_time);
update arvovalikonvertteriparametri_history set update_time = upper(system_time) where system_time != 'empty';
update arvovalikonvertteriparametri_history set update_time = now() where system_time = 'empty';
alter table arvovalikonvertteriparametri_history drop column if exists system_time;
alter table arvovalikonvertteriparametri_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table funktioargumentti_history add column if not exists update_time timestamptz;
create index if not exists funktioargumentti_history_update_time_idx ON funktioargumentti_history (update_time);
update funktioargumentti_history set update_time = upper(system_time) where system_time != 'empty';
update funktioargumentti_history set update_time = now() where system_time = 'empty';
alter table funktioargumentti_history drop column if exists system_time;
alter table funktioargumentti_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table funktiokutsu_history add column if not exists update_time timestamptz;
create index if not exists funktiokutsu_history_update_time_idx ON funktiokutsu_history (update_time);
update funktiokutsu_history set update_time = upper(system_time) where system_time != 'empty';
update funktiokutsu_history set update_time = now() where system_time = 'empty';
alter table funktiokutsu_history drop column if exists system_time;
alter table funktiokutsu_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table hakukohteen_valintaperuste_history add column if not exists update_time timestamptz;
create index if not exists hakukohteen_valintaperuste_history_update_time_idx ON hakukohteen_valintaperuste_history (update_time);
update hakukohteen_valintaperuste_history set update_time = upper(system_time) where system_time != 'empty';
update hakukohteen_valintaperuste_history set update_time = now() where system_time = 'empty';
alter table hakukohteen_valintaperuste_history drop column if exists system_time;
alter table hakukohteen_valintaperuste_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table jarjestyskriteeri_history add column if not exists update_time timestamptz;
create index if not exists jarjestyskriteeri_history_update_time_idx ON jarjestyskriteeri_history (update_time);
update jarjestyskriteeri_history set update_time = upper(system_time) where system_time != 'empty';
update jarjestyskriteeri_history set update_time = now() where system_time = 'empty';
alter table jarjestyskriteeri_history drop column if exists system_time;
alter table jarjestyskriteeri_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table laskentakaava_history add column if not exists update_time timestamptz;
create index if not exists laskentakaava_history_update_time_idx ON laskentakaava_history (update_time);
update laskentakaava_history set update_time = upper(system_time) where system_time != 'empty';
update laskentakaava_history set update_time = now() where system_time = 'empty';
alter table laskentakaava_history drop column if exists system_time;
alter table laskentakaava_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table lokalisoitu_teksti_history add column if not exists update_time timestamptz;
create index if not exists lokalisoitu_teksti_history_update_time_idx ON lokalisoitu_teksti_history (update_time);
update lokalisoitu_teksti_history set update_time = upper(system_time) where system_time != 'empty';
update lokalisoitu_teksti_history set update_time = now() where system_time = 'empty';
alter table lokalisoitu_teksti_history drop column if exists system_time;
alter table lokalisoitu_teksti_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table syoteparametri_history add column if not exists update_time timestamptz;
create index if not exists syoteparametri_history_update_time_idx ON syoteparametri_history (update_time);
update syoteparametri_history set update_time = upper(system_time) where system_time != 'empty';
update syoteparametri_history set update_time = now() where system_time = 'empty';
alter table syoteparametri_history drop column if exists system_time;
alter table syoteparametri_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table syotettavanarvonkoodi_history add column if not exists update_time timestamptz;
create index if not exists syotettavanarvonkoodi_history_update_time_idx ON syotettavanarvonkoodi_history (update_time);
update syotettavanarvonkoodi_history set update_time = upper(system_time) where system_time != 'empty';
update syotettavanarvonkoodi_history set update_time = now() where system_time = 'empty';
alter table syotettavanarvonkoodi_history drop column if exists system_time;
alter table syotettavanarvonkoodi_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table tekstiryhma_history add column if not exists update_time timestamptz;
create index if not exists tekstiryhma_history_update_time_idx ON tekstiryhma_history (update_time);
update tekstiryhma_history set update_time = upper(system_time) where system_time != 'empty';
update tekstiryhma_history set update_time = now() where system_time = 'empty';
alter table tekstiryhma_history drop column if exists system_time;
alter table tekstiryhma_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table valinnan_vaihe_history add column if not exists update_time timestamptz;
create index if not exists valinnan_vaihe_history_update_time_idx ON valinnan_vaihe_history (update_time);
update valinnan_vaihe_history set update_time = upper(system_time) where system_time != 'empty';
update valinnan_vaihe_history set update_time = now() where system_time = 'empty';
alter table valinnan_vaihe_history drop column if exists system_time;
alter table valinnan_vaihe_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table valintakoe_history add column if not exists update_time timestamptz;
create index if not exists valintakoe_history_update_time_idx ON valintakoe_history (update_time);
update valintakoe_history set update_time = upper(system_time) where system_time != 'empty';
update valintakoe_history set update_time = now() where system_time = 'empty';
alter table valintakoe_history drop column if exists system_time;
alter table valintakoe_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table valintaperuste_viite_history add column if not exists update_time timestamptz;
create index if not exists valintaperuste_viite_history_update_time_idx ON valintaperuste_viite_history (update_time);
update valintaperuste_viite_history set update_time = upper(system_time) where system_time != 'empty';
update valintaperuste_viite_history set update_time = now() where system_time = 'empty';
alter table valintaperuste_viite_history drop column if exists system_time;
alter table valintaperuste_viite_history
    alter column update_time set default now(),
    alter column update_time set not null;

alter table valintatapajono_history add column if not exists update_time timestamptz;
create index if not exists valintatapajono_history_update_time_idx ON valintatapajono_history (update_time);
update valintatapajono_history set update_time = upper(system_time) where system_time != 'empty';
update valintatapajono_history set update_time = now() where system_time = 'empty';
alter table valintatapajono_history drop column if exists system_time;
alter table valintatapajono_history
    alter column update_time set default now(),
    alter column update_time set not null;

-- Historian update-funktiot
create or replace function update_hakukohde_viite_history() returns trigger
    language plpgsql
as $$
begin
    insert into hakukohde_viite_history (
        id,
        version,
        hakuoid,
        nimi,
        oid,
        hakukohdekoodi_id,
        valintaryhma_id,
        manuaalisesti_siirretty,
        tarjoajaoid,
        tila,
        transaction_id
    )
    values (
        old.id,
        old.version,
        old.hakuoid,
        old.nimi,
        old.oid,
        old.hakukohdekoodi_id,
        old.valintaryhma_id,
        old.manuaalisesti_siirretty,
        old.tarjoajaoid,
        old.tila,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_hakukohteen_valintaperuste_history() returns trigger
    language plpgsql
as $$
begin
    insert into hakukohteen_valintaperuste_history (
        id,
        version,
        arvo,
        kuvaus,
        tunniste,
        hakukohde_viite_id,
        transaction_id
    )
    values (
        old.id,
        old.version,
        old.arvo,
        old.kuvaus,
        old.tunniste,
        old.hakukohde_viite_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_valintaperuste_viite_history() returns trigger
    language plpgsql
as $$
begin
    insert into valintaperuste_viite_history (
        id,
        version,
        kuvaus,
        lahde,
        on_pakollinen,
        tunniste,
        funktiokutsu_id,
        epasuora_viittaus,
        indeksi,
        tekstiryhma_id,
        vaatii_osallistumisen,
        syotettavissa_kaikille,
        syotettavanarvontyyppi_id,
        tilastoidaan,
        transaction_id
    )
    values (
        old.id,
        old.version,
        old.kuvaus,
        old.lahde,
        old.on_pakollinen,
        old.tunniste,
        old.funktiokutsu_id,
        old.epasuora_viittaus,
        old.indeksi,
        old.tekstiryhma_id,
        old.vaatii_osallistumisen,
        old.syotettavissa_kaikille,
        old.syotettavanarvontyyppi_id,
        old.tilastoidaan,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_syotettavanarvonkoodi_history() returns trigger
    language plpgsql
as $$
begin
    insert into syotettavanarvonkoodi_history (
        id,
        version,
        arvo,
        nimi_en,
        nimi_fi,
        nimi_sv,
        uri,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.arvo,
        old.nimi_en,
        old.nimi_fi,
        old.nimi_sv,
        old.uri,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_valinnan_vaihe_history() returns trigger
    language plpgsql
as $$
begin
    insert into valinnan_vaihe_history (
        id,
        version,
        aktiivinen,
        kuvaus,
        nimi,
        oid,
        valinnan_vaihe_tyyppi,
        edellinen_valinnan_vaihe_id,
        hakukohde_viite_id,
        master_valinnan_vaihe_id,
        valintaryhma_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.aktiivinen,
        old.kuvaus,
        old.nimi,
        old.oid,
        old.valinnan_vaihe_tyyppi,
        old.edellinen_valinnan_vaihe_id,
        old.hakukohde_viite_id,
        old.master_valinnan_vaihe_id,
        old.valintaryhma_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_valintatapajono_history() returns trigger
    language plpgsql
as $$
begin
    insert into valintatapajono_history (
        id,
        version,
        aktiivinen,
        aloituspaikat,
        kuvaus,
        nimi,
        oid,
        siirretaan_sijoitteluun,
        tasapistesaanto,
        edellinen_valintatapajono_id,
        master_valintatapajono_id,
        valinnan_vaihe_id,
        ei_varasijatayttoa,
        poissa_oleva_taytto,
        varasijat,
        varasijan_tayttojono_id,
        varasijoja_kaytetaan_alkaen,
        varasijoja_taytetaan_asti,
        kaytetaan_valintalaskentaa,
        kaikki_ehdon_tayttavat_hyvaksytaan,
        valisijoittelu,
        automaattinen_sijoitteluun_siirto,
        poistetaanko_hylatyt,
        tyyppi,
        ei_lasketa_paivamaaran_jalkeen,
        merkitse_myoh_auto,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.aktiivinen,
        old.aloituspaikat,
        old.kuvaus,
        old.nimi,
        old.oid,
        old.siirretaan_sijoitteluun,
        old.tasapistesaanto,
        old.edellinen_valintatapajono_id,
        old.master_valintatapajono_id,
        old.valinnan_vaihe_id,
        old.ei_varasijatayttoa,
        old.poissa_oleva_taytto,
        old.varasijat,
        old.varasijan_tayttojono_id,
        old.varasijoja_kaytetaan_alkaen,
        old.varasijoja_taytetaan_asti,
        old.kaytetaan_valintalaskentaa,
        old.kaikki_ehdon_tayttavat_hyvaksytaan,
        old.valisijoittelu,
        old.automaattinen_sijoitteluun_siirto,
        old.poistetaanko_hylatyt,
        old.tyyppi,
        old.ei_lasketa_paivamaaran_jalkeen,
        old.merkitse_myoh_auto,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_jarjestyskriteeri_history() returns trigger
    language plpgsql
as $$
begin
    insert into jarjestyskriteeri_history (
        id,
        version,
        aktiivinen,
        metatiedot,
        oid,
        edellinen_jarjestyskriteeri_id,
        laskentakaava_id,
        master_jarjestyskriteeri_id,
        valintatapajono_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.aktiivinen,
        old.metatiedot,
        old.oid,
        old.edellinen_jarjestyskriteeri_id,
        old.laskentakaava_id,
        old.master_jarjestyskriteeri_id,
        old.valintatapajono_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_laskentakaava_history() returns trigger
    language plpgsql
as $$
begin
    insert into laskentakaava_history (
        id,
        version,
        kuvaus,
        nimi,
        on_luonnos,
        tyyppi,
        funktiokutsu_id,
        hakukohdeviite,
        valintaryhmaviite,
        kopio_laskentakaavasta_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.kuvaus,
        old.nimi,
        old.on_luonnos,
        old.tyyppi,
        old.funktiokutsu_id,
        old.hakukohdeviite,
        old.valintaryhmaviite,
        old.kopio_laskentakaavasta_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_funktiokutsu_history() returns trigger
    language plpgsql
as $$
begin
    insert into funktiokutsu_history (
        id,
        version,
        funktionimi,
        tallenna_tulos,
        tulos_tunniste,
        tulos_teksti_fi,
        tulos_teksti_sv,
        tulos_teksti_en,
        oma_opintopolku,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.funktionimi,
        old.tallenna_tulos,
        old.tulos_tunniste,
        old.tulos_teksti_fi,
        old.tulos_teksti_sv,
        old.tulos_teksti_en,
        old.oma_opintopolku,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_funktioargumentti_history() returns trigger
    language plpgsql
as $$
begin
    insert into funktioargumentti_history (
        id,
        version,
        indeksi,
        funktiokutsuchild_id,
        laskentakaavachild_id,
        funktiokutsuparent_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.indeksi,
        old.funktiokutsuchild_id,
        old.laskentakaavachild_id,
        old.funktiokutsuparent_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_syoteparametri_history() returns trigger
    language plpgsql
as $$
begin
    insert into syoteparametri_history (
        id,
        version,
        arvo,
        avain,
        funktiokutsu_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.arvo,
        old.avain,
        old.funktiokutsu_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_arvokonvertteriparametri_history() returns trigger
    language plpgsql
as $$
begin
    insert into arvokonvertteriparametri_history (
        id,
        version,
        hylkaysperuste,
        paluuarvo,
        arvo,
        funktiokutsu_id,
        tekstiryhma_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.hylkaysperuste,
        old.paluuarvo,
        old.arvo,
        old.funktiokutsu_id,
        old.tekstiryhma_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_arvovalikonvertteriparametri_history() returns trigger
    language plpgsql
as $$
begin
    insert into arvovalikonvertteriparametri_history (
        id,
        version,
        paluuarvo,
        maxvalue,
        minvalue,
        palauta_haettu_arvo,
        funktiokutsu_id,
        hylkaysperuste,
        tekstiryhma_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.paluuarvo,
        old.maxvalue,
        old.minvalue,
        old.palauta_haettu_arvo,
        old.funktiokutsu_id,
        old.hylkaysperuste,
        old.tekstiryhma_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_tekstiryhma_history() returns trigger
    language plpgsql
as $$
begin
    insert into tekstiryhma_history (
        id,
        version,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_lokalisoitu_teksti_history() returns trigger
    language plpgsql
as $$
begin
    insert into lokalisoitu_teksti_history (
        id,
        version,
        kieli,
        teksti,
        tekstiryhma_id,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.kieli,
        old.teksti,
        old.tekstiryhma_id,
        old.transaction_id
    );
    return null;
end;
$$;

create or replace function update_valintakoe_history() returns trigger
    language plpgsql
as $$
begin
    insert into valintakoe_history (
        id,
        version,
        aktiivinen,
        kuvaus,
        nimi,
        oid,
        tunniste,
        laskentakaava_id,
        master_valintakoe_id,
        valinnan_vaihe_id,
        lahetetaanko_koekutsut,
        kutsutaanko_kaikki,
        kutsuttavien_maara,
        kutsun_kohde,
        kutsun_kohde_avain,
        transaction_id
    ) values (
        old.id,
        old.version,
        old.aktiivinen,
        old.kuvaus,
        old.nimi,
        old.oid,
        old.tunniste,
        old.laskentakaava_id,
        old.master_valintakoe_id,
        old.valinnan_vaihe_id,
        old.lahetetaanko_koekutsut,
        old.kutsutaanko_kaikki,
        old.kutsuttavien_maara,
        old.kutsun_kohde,
        old.kutsun_kohde_avain,
        old.transaction_id
    );
    return null;
end;
$$;
