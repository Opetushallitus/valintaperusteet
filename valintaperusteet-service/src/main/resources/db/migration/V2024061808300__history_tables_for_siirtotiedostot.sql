-- Historiataulut
create table hakukohde_viite_history (like hakukohde_viite);
alter table hakukohde_viite_history drop column if exists last_modified;
alter table hakukohde_viite_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table hakukohteen_valintaperuste_history (like hakukohteen_valintaperuste);
alter table hakukohteen_valintaperuste_history drop column if exists last_modified;
alter table hakukohteen_valintaperuste_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table valintaperuste_viite_history (like valintaperuste_viite);
alter table valintaperuste_viite_history drop column if exists last_modified;
alter table valintaperuste_viite_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table syotettavanarvonkoodi_history (like syotettavanarvonkoodi);
alter table syotettavanarvonkoodi_history drop column if exists last_modified;
alter table syotettavanarvonkoodi_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table valinnan_vaihe_history (like valinnan_vaihe);
alter table valinnan_vaihe_history drop column if exists last_modified;
alter table valinnan_vaihe_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table valintatapajono_history (like valintatapajono);
alter table valintatapajono_history drop column if exists last_modified;
alter table valintatapajono_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table jarjestyskriteeri_history (like jarjestyskriteeri);
alter table jarjestyskriteeri_history drop column if exists last_modified;
alter table jarjestyskriteeri_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table laskentakaava_history (like laskentakaava);
alter table laskentakaava_history drop column if exists last_modified;
alter table laskentakaava_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table funktiokutsu_history (like funktiokutsu);
alter table funktiokutsu_history drop column if exists last_modified;
alter table funktiokutsu_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table funktioargumentti_history (like funktioargumentti);
alter table funktioargumentti_history drop column if exists last_modified;
alter table funktioargumentti_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table syoteparametri_history (like syoteparametri);
alter table syoteparametri_history drop column if exists last_modified;
alter table syoteparametri_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table arvokonvertteriparametri_history (like arvokonvertteriparametri);
alter table arvokonvertteriparametri_history drop column if exists last_modified;
alter table arvokonvertteriparametri_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table arvovalikonvertteriparametri_history (like arvovalikonvertteriparametri);
alter table arvovalikonvertteriparametri_history drop column if exists last_modified;
alter table arvovalikonvertteriparametri_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table tekstiryhma_history (like tekstiryhma);
alter table tekstiryhma_history drop column if exists last_modified;
alter table tekstiryhma_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table lokalisoitu_teksti_history (like lokalisoitu_teksti);
alter table lokalisoitu_teksti_history drop column if exists last_modified;
alter table lokalisoitu_teksti_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

create table valintakoe_history (like valintakoe);
alter table valintakoe_history drop column if exists last_modified;
alter table valintakoe_history
    add column if not exists system_time tstzrange not null default tstzrange(now(), null, '[)');

-- Historian päivitysfunktiot
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    )
    values (
        old.id,
        old.version,
        old.arvo,
        old.kuvaus,
        old.tunniste,
        old.hakukohde_viite_id,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.arvo,
        old.nimi_en,
        old.nimi_fi,
        old.nimi_sv,
        old.uri,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.indeksi,
        old.funktiokutsuchild_id,
        old.laskentakaavachild_id,
        old.funktiokutsuparent_id,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.arvo,
        old.avain,
        old.funktiokutsu_id,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.hylkaysperuste,
        old.paluuarvo,
        old.arvo,
        old.funktiokutsu_id,
        old.tekstiryhma_id,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
    ) values (
        old.id,
        old.version,
        old.kieli,
        old.teksti,
        old.tekstiryhma_id,
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
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
        transaction_id,
        system_time
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
        old.transaction_id,
        tstzrange(old.last_modified, now(), '[)')
    );
    return null;
end;
$$;

-- Historianpäivitys triggerit
create trigger delete_hakukohde_viite_history
    after delete
    on hakukohde_viite
    for each row
    execute procedure update_hakukohde_viite_history();

create trigger update_hakukohde_viite_history
    after update
    on hakukohde_viite
    for each row
    execute procedure update_hakukohde_viite_history();


create trigger delete_hakukohteen_valintaperuste_history
    after delete
    on hakukohteen_valintaperuste
    for each row
    execute procedure update_hakukohteen_valintaperuste_history();

create trigger update_hakukohteen_valintaperuste_history
    after update
    on hakukohteen_valintaperuste
    for each row
    execute procedure update_hakukohteen_valintaperuste_history();


create trigger delete_valintaperuste_viite_history
    after delete
    on valintaperuste_viite
    for each row
    execute procedure update_valintaperuste_viite_history();

create trigger update_valintaperuste_viite_history
    after update
    on valintaperuste_viite
    for each row
    execute procedure update_valintaperuste_viite_history();


create trigger delete_syotettavanarvonkoodi_history
    after delete
    on syotettavanarvonkoodi
    for each row
    execute procedure update_syotettavanarvonkoodi_history();

create trigger update_syotettavanarvonkoodi_history
    after update
    on syotettavanarvonkoodi
    for each row
    execute procedure update_syotettavanarvonkoodi_history();


create trigger delete_valinnan_vaihe_history
    after delete
    on valinnan_vaihe
    for each row
    execute procedure update_valinnan_vaihe_history();

create trigger update_valinnan_vaihe_history
    after update
    on valinnan_vaihe
    for each row
    execute procedure update_valinnan_vaihe_history();


create trigger delete_valintatapajono_history
    after delete
    on valintatapajono
    for each row
    execute procedure update_valintatapajono_history();

create trigger update_valintatapajono_history
    after update
    on valintatapajono
    for each row
    execute procedure update_valintatapajono_history();


create trigger delete_jarjestyskriteeri_history
    after delete
    on jarjestyskriteeri
    for each row
    execute procedure update_jarjestyskriteeri_history();

create trigger update_jarjestyskriteeri_history
    after update
    on jarjestyskriteeri
    for each row
    execute procedure update_jarjestyskriteeri_history();


create trigger delete_laskentakaava_history
    after delete
    on laskentakaava
    for each row
    execute procedure update_laskentakaava_history();

create trigger update_laskentakaava_history
    after update
    on laskentakaava
    for each row
    execute procedure update_laskentakaava_history();


create trigger delete_funktiokutsu_history
    after delete
    on funktiokutsu
    for each row
    execute procedure update_funktiokutsu_history();

create trigger update_funktiokutsu_history
    after update
    on funktiokutsu
    for each row
    execute procedure update_funktiokutsu_history();


create trigger delete_funktioargumentti_history
    after delete
    on funktioargumentti
    for each row
    execute procedure update_funktioargumentti_history();

create trigger update_funktioargumentti_history
    after update
    on funktioargumentti
    for each row
    execute procedure update_funktioargumentti_history();


create trigger delete_syoteparametri_history
    after delete
    on syoteparametri
    for each row
    execute procedure update_syoteparametri_history();

create trigger update_syoteparametri_history
    after update
    on syoteparametri
    for each row
    execute procedure update_syoteparametri_history();


create trigger delete_arvokonvertteriparametri_history
    after delete
    on arvokonvertteriparametri
    for each row
    execute procedure update_arvokonvertteriparametri_history();

create trigger update_arvokonvertteriparametri_history
    after update
    on arvokonvertteriparametri
    for each row
    execute procedure update_arvokonvertteriparametri_history();


create trigger delete_arvovalikonvertteriparametri_history
    after delete
    on arvovalikonvertteriparametri
    for each row
    execute procedure update_arvovalikonvertteriparametri_history();

create trigger update_arvovalikonvertteriparametri_history
    after update
    on arvovalikonvertteriparametri
    for each row
    execute procedure update_arvovalikonvertteriparametri_history();


create trigger delete_tekstiryhma_history
    after delete
    on tekstiryhma
    for each row
    execute procedure update_tekstiryhma_history();

create trigger update_tekstiryhma_history
    after update
    on tekstiryhma
    for each row
    execute procedure update_tekstiryhma_history();


create trigger delete_lokalisoitu_teksti_history
    after delete
    on lokalisoitu_teksti
    for each row
    execute procedure update_lokalisoitu_teksti_history();

create trigger update_lokalisoitu_teksti_history
    after update
    on lokalisoitu_teksti
    for each row
    execute procedure update_lokalisoitu_teksti_history();


create trigger delete_valintakoe_history
    after delete
    on valintakoe
    for each row
    execute procedure update_valintakoe_history();

create trigger update_valintakoe_history
    after update
    on valintakoe
    for each row
    execute procedure update_valintakoe_history();
