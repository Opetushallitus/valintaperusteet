insert into hakukohde_viite (id, version, hakuoid, nimi, oid, manuaalisesti_siirretty, tarjoajaoid, tila)
values (1, 1, '1.2.246.562.29.00000000001', 'Testi hakukohde 1', '1.2.246.562.20.00000000001', false, '1.2.246.562.10.00000000001', 'JULKAISTU');
insert into hakukohde_viite (id, version, hakuoid, nimi, oid, manuaalisesti_siirretty, tarjoajaoid, tila)
values (2, 2, '1.2.246.562.29.00000000002', 'Testi hakukohde 2', '1.2.246.562.20.00000000002', false, '1.2.246.562.10.00000000002', 'JULKAISTU');
insert into hakukohde_viite (id, version, hakuoid, nimi, oid, manuaalisesti_siirretty, tarjoajaoid, tila)
values (3, 3, '1.2.246.562.29.00000000003', 'Testi hakukohde 3', '1.2.246.562.20.00000000003', false, '1.2.246.562.10.00000000003', 'JULKAISTU');
insert into hakukohde_viite (id, version, hakuoid, nimi, oid, manuaalisesti_siirretty, tarjoajaoid, tila)
values (4, 4, '1.2.246.562.29.00000000004', 'Testi hakukohde 4', '1.2.246.562.20.00000000004', false, '1.2.246.562.10.00000000004', 'JULKAISTU');
insert into hakukohde_viite (id, version, hakuoid, nimi, oid, manuaalisesti_siirretty, tarjoajaoid, tila)
values (5, 5, '1.2.246.562.29.00000000005', 'Testi hakukohde 5', '1.2.246.562.20.00000000005', false, '1.2.246.562.10.00000000005', 'JULKAISTU');



insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (10, 1, true, 'Testi valinnanvaihe tavallinen 10', 'Testi valinnanvaihe tavallinen 10', '11111111', 'TAVALLINEN', null, 1);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (11, 1, true, 'Testi valinnanvaihe valintakoe 11', 'Testi valinnanvaihe valintakoe 11', '22222222', 'VALINTAKOE', 10, 1);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (12, 1, true, 'Testi valinnanvaihe tavallinen 12', 'Testi valinnanvaihe tavallinen 12', '33333333', 'TAVALLINEN', 11, 1);

insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (20, 1, true, 'Testi valinnanvaihe tavallinen 20', 'Testi valinnanvaihe tavallinen 20', '11111110', 'TAVALLINEN', null, 2);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (21, 1, true, 'Testi valinnanvaihe valintakoe 21', 'Testi valinnanvaihe valintakoe 21', '22222220', 'VALINTAKOE', 20, 2);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (22, 1, true, 'Testi valinnanvaihe tavallinen 22', 'Testi valinnanvaihe tavallinen 22', '33333330', 'TAVALLINEN', 21, 2);

insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (30, 1, true, 'Testi valinnanvaihe tavallinen 30', 'Testi valinnanvaihe tavallinen 30', '111111101', 'TAVALLINEN', null, 3);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (31, 1, true, 'Testi valinnanvaihe valintakoe 31', 'Testi valinnanvaihe valintakoe 31', '222222201', 'VALINTAKOE', 30, 3);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (32, 1, true, 'Testi valinnanvaihe tavallinen 32', 'Testi valinnanvaihe tavallinen 32', '333333301', 'TAVALLINEN', 31, 3);

insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (40, 1, true, 'Testi valinnanvaihe tavallinen 40', 'Testi valinnanvaihe tavallinen 40', '111111102', 'TAVALLINEN', null, 4);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (41, 1, true, 'Testi valinnanvaihe valintakoe 41', 'Testi valinnanvaihe valintakoe 41', '222222202', 'VALINTAKOE', 40, 4);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (42, 1, true, 'Testi valinnanvaihe tavallinen 42', 'Testi valinnanvaihe tavallinen 42', '333333302', 'TAVALLINEN', 41, 4);

insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (50, 1, true, 'Testi valinnanvaihe tavallinen 50', 'Testi valinnanvaihe tavallinen 50', '111111103', 'TAVALLINEN', null, 5);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (51, 1, true, 'Testi valinnanvaihe valintakoe 51', 'Testi valinnanvaihe valintakoe 51', '222222203', 'VALINTAKOE', 50, 5);
insert into valinnan_vaihe (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi, edellinen_valinnan_vaihe_id, hakukohde_viite_id)
values (52, 1, true, 'Testi valinnanvaihe tavallinen 52', 'Testi valinnanvaihe tavallinen 52', '333333303', 'TAVALLINEN', 51, 5);



insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (100, 1, true, 1, 'valintatapajono 100', 'valintatapajono 100', '11111111-0', true, 'ARVONTA', 10, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (102, 1, true, 2, 'valintatapajono 102', 'valintatapajono 102', '11111111-1', true, 'ARVONTA', 12, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (200, 1, true, 3, 'valintatapajono 200', 'valintatapajono 200', '11111111-3', true, 'ARVONTA', 20, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (202, 1, true, 4, 'valintatapajono 202', 'valintatapajono 202', '11111111-4', true, 'ARVONTA', 22, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (300, 1, true, 5, 'valintatapajono 300', 'valintatapajono 300', '11111111-5', true, 'ARVONTA', 30, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (302, 1, true, 6, 'valintatapajono 302', 'valintatapajono 302', '11111111-6', true, 'ARVONTA', 32, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (400, 1, true, 7, 'valintatapajono 400', 'valintatapajono 400', '11111111-7', true, 'ARVONTA', 40, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (402, 1, true, 8, 'valintatapajono 402', 'valintatapajono 402', '11111111-8', true, 'ARVONTA', 42, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (500, 1, true, 9, 'valintatapajono 500', 'valintatapajono 500', '11111111-9', true, 'ARVONTA', 50, false, true, 0, true, false, false, true, false, false );
insert into valintatapajono (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun, tasapistesaanto, valinnan_vaihe_id,
                             ei_varasijatayttoa, poissa_oleva_taytto, varasijat, kaytetaan_valintalaskentaa, kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu,
                             automaattinen_sijoitteluun_siirto, poistetaanko_hylatyt, merkitse_myoh_auto)
values (502, 1, true, 10, 'valintatapajono 502', 'valintatapajono 502', '11111111-10', true, 'ARVONTA', 52, false, true, 0, true, false, false, true, false, false );



insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10000, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10001, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10002, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10003, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11000, 1, 'syoteparametrin arvo 11000 funktiokutsulle 10000', 'avain', 10000);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11001, 1, 'syoteparametrin arvo 11001 funktiokutsulle 10001', 'avain', 10001);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11002, 1, 'syoteparametrin arvo 11002 funktiokutsulle 10002', 'avain', 10002);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11003, 1, 'syoteparametrin arvo 11003 funktiokutsulle 10003', 'avain', 10003);

insert into tekstiryhma (id, version) values (15000, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15100, 0, 'FI', 'Teksti arvokonvertteri 12000', 15000);
insert into tekstiryhma (id, version) values (15001, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15101, 0, 'FI', 'Teksti arvovalikonvertteri 13000', 15001);
insert into tekstiryhma (id, version) values (15002, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15102, 0, 'FI', 'Teksti valintaperuste_viite 14000', 15002);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12000, 1, false, '1', '1', 10001, 15000);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
    values (13000, 1, '1', '1', '1', true, 10002, false, 15001);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (14100, 1, '1', 'syotettavanarvonkoodi_14100');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14000, 1, 'Valintaperuste_viite 14000 funktiokutsulle 10003', 'HAETTAVA_ARVO', false, 'ET', 10003, false, 2, 15002, true, false, 14100, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2000, 0, 'laskentakaava 2000 funktiokutsulle 10000', 'laskentakaava 2000 funktiokutsulle 10000', false, 'LUKUARVOFUNKTIO', 10000);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2001, 0, 'laskentakaava 2001 funktiokutsulle 10001', 'laskentakaava 2001 funktiokutsulle 10001', false, 'LUKUARVOFUNKTIO', 10001);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2002, 0, 'laskentakaava 2002 funktiokutsulle 10002', 'laskentakaava 2002 funktiokutsulle 10002', false, 'LUKUARVOFUNKTIO', 10002);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2003, 0, 'laskentakaava 2003 funktiokutsulle 10003', 'laskentakaava 2003 funktiokutsulle 10003', false, 'LUKUARVOFUNKTIO', 10003);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10010, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10011, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10012, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11011, 1, 'syoteparametrin arvo 11011 funktiokutsulle 10011', 'avain', 10011);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11012, 1, 'syoteparametrin arvo 11012 funktiokutsulle 10012', 'avain', 10012);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2010, 0, 'laskentakaava 2010 funktiokutsulle 10010', 'laskentakaava 2010 funktiokutsulle 10010', false, 'TOTUUSARVOFUNKTIO', 10010);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16010, 1, 1, 10011, null, 10010);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16011, 1, 1, 10012, null, 10010);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10020, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10021, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10022, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11022, 1, 'syoteparametrin arvo 11022 funktiokutsulle 10022', 'avain', 10022);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2020, 0, 'laskentakaava 2020 funktiokutsulle 10020', 'laskentakaava 2020 funktiokutsulle 10020', false, 'TOTUUSARVOFUNKTIO', 10020);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2021, 0, 'laskentakaava 2021 funktiokutsulle 10021', 'laskentakaava 2021 funktiokutsulle 10021', false, 'LUKUARVOFUNKTIO', 10021);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16020, 1, 1, null, 2021, 10020);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16021, 1, 1, 10022, null, 10021);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10030, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10031, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10032, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10033, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11030, 1, 'syoteparametrin arvo 11030 funktiokutsulle 10030', 'avain', 10030);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11031, 1, 'syoteparametrin arvo 11031 funktiokutsulle 10031', 'avain', 10031);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11032, 1, 'syoteparametrin arvo 11032 funktiokutsulle 10032', 'avain', 10032);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11033, 1, 'syoteparametrin arvo 11033 funktiokutsulle 10033', 'avain', 10033);

insert into tekstiryhma (id, version) values (15030, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15130, 0, 'FI', 'Teksti arvokonvertteri 12030', 15030);
insert into tekstiryhma (id, version) values (15031, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15131, 0, 'FI', 'Teksti arvovalikonvertteri 13030', 15031);
insert into tekstiryhma (id, version) values (15032, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15132, 0, 'FI', 'Teksti valintaperuste_viite 14030', 15032);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12030, 1, false, '1', '1', 10031, 15030);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
values (13030, 1, '1', '1', '1', true, 10032, false, 15031);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (14130, 1, '1', 'syotettavanarvonkoodi_14130');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14030, 1, 'Valintaperuste_viite 14030 funktiokutsulle 10033', 'HAETTAVA_ARVO', false, 'ET', 10033, false, 2, 15032, true, false, 14130, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2030, 0, 'laskentakaava 2030 funktiokutsulle 10030', 'laskentakaava 2030 funktiokutsulle 10030', false, 'LUKUARVOFUNKTIO', 10030);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2031, 0, 'laskentakaava 2031 funktiokutsulle 10031', 'laskentakaava 2031 funktiokutsulle 10031', false, 'LUKUARVOFUNKTIO', 10031);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2032, 0, 'laskentakaava 2032 funktiokutsulle 10032', 'laskentakaava 2032 funktiokutsulle 10032', false, 'LUKUARVOFUNKTIO', 10032);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2033, 0, 'laskentakaava 2033 funktiokutsulle 10033', 'laskentakaava 2033 funktiokutsulle 10033', false, 'LUKUARVOFUNKTIO', 10033);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10040, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10041, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10042, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11041, 1, 'syoteparametrin arvo 11041 funktiokutsulle 10041', 'avain', 10041);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11042, 1, 'syoteparametrin arvo 11042 funktiokutsulle 10042', 'avain', 10042);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2040, 0, 'laskentakaava 2040 funktiokutsulle 10040', 'laskentakaava 2040 funktiokutsulle 10040', false, 'TOTUUSARVOFUNKTIO', 10040);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16040, 1, 1, 10041, null, 10040);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16041, 1, 1, 10042, null, 10040);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10050, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10051, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10052, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11052, 1, 'syoteparametrin arvo 11052 funktiokutsulle 10052', 'avain', 10052);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2050, 0, 'laskentakaava 2050 funktiokutsulle 10050', 'laskentakaava 2050 funktiokutsulle 10050', false, 'TOTUUSARVOFUNKTIO', 10050);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2051, 0, 'laskentakaava 2051 funktiokutsulle 10051', 'laskentakaava 2051 funktiokutsulle 10051', false, 'LUKUARVOFUNKTIO', 10051);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16050, 1, 1, null, 2051, 10050);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16051, 1, 1, 10052, null, 10051);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10060, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10061, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10062, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10063, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11060, 1, 'syoteparametrin arvo 11060 funktiokutsulle 10060', 'avain', 10060);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11061, 1, 'syoteparametrin arvo 11061 funktiokutsulle 10061', 'avain', 10061);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11062, 1, 'syoteparametrin arvo 11062 funktiokutsulle 10062', 'avain', 10062);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11063, 1, 'syoteparametrin arvo 11063 funktiokutsulle 10063', 'avain', 10063);

insert into tekstiryhma (id, version) values (15060, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15160, 0, 'FI', 'Teksti arvokonvertteri 12060', 15060);
insert into tekstiryhma (id, version) values (15061, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15161, 0, 'FI', 'Teksti arvovalikonvertteri 13060', 15061);
insert into tekstiryhma (id, version) values (15062, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15162, 0, 'FI', 'Teksti valintaperuste_viite 14060', 15062);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12060, 1, false, '1', '1', 10061, 15060);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
values (13060, 1, '1', '1', '1', true, 10062, false, 15061);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (14160, 1, '1', 'syotettavanarvonkoodi_14160');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14060, 1, 'Valintaperuste_viite 14060 funktiokutsulle 10063', 'HAETTAVA_ARVO', false, 'ET', 10063, false, 2, 15062, true, false, 14160, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2060, 0, 'laskentakaava 2060 funktiokutsulle 10060', 'laskentakaava 2060 funktiokutsulle 10060', false, 'LUKUARVOFUNKTIO', 10060);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2061, 0, 'laskentakaava 2061 funktiokutsulle 10061', 'laskentakaava 2061 funktiokutsulle 10061', false, 'LUKUARVOFUNKTIO', 10061);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2062, 0, 'laskentakaava 2062 funktiokutsulle 10062', 'laskentakaava 2062 funktiokutsulle 10062', false, 'LUKUARVOFUNKTIO', 10062);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2063, 0, 'laskentakaava 2063 funktiokutsulle 10063', 'laskentakaava 2063 funktiokutsulle 10063', false, 'LUKUARVOFUNKTIO', 10063);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10070, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10071, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10072, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11071, 1, 'syoteparametrin arvo 11071 funktiokutsulle 10071', 'avain', 10071);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11072, 1, 'syoteparametrin arvo 11072 funktiokutsulle 10072', 'avain', 10072);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2070, 0, 'laskentakaava 2070 funktiokutsulle 10070', 'laskentakaava 2070 funktiokutsulle 10070', false, 'TOTUUSARVOFUNKTIO', 10070);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16070, 1, 1, 10071, null, 10070);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16071, 1, 1, 10072, null, 10070);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10080, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10081, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10082, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11082, 1, 'syoteparametrin arvo 11082 funktiokutsulle 10082', 'avain', 10082);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2080, 0, 'laskentakaava 2080 funktiokutsulle 10080', 'laskentakaava 2080 funktiokutsulle 10080', false, 'TOTUUSARVOFUNKTIO', 10080);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2081, 0, 'laskentakaava 2081 funktiokutsulle 10081', 'laskentakaava 2081 funktiokutsulle 10081', false, 'LUKUARVOFUNKTIO', 10081);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16080, 1, 1, null, 2081, 10080);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16081, 1, 1, 10082, null, 10081);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10090, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10091, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10092, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10093, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11090, 1, 'syoteparametrin arvo 11090 funktiokutsulle 10090', 'avain', 10090);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11091, 1, 'syoteparametrin arvo 11091 funktiokutsulle 10091', 'avain', 10091);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11092, 1, 'syoteparametrin arvo 11092 funktiokutsulle 10092', 'avain', 10092);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11093, 1, 'syoteparametrin arvo 11093 funktiokutsulle 10093', 'avain', 10093);

insert into tekstiryhma (id, version) values (15090, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15190, 0, 'FI', 'Teksti arvokonvertteri 12090', 15090);
insert into tekstiryhma (id, version) values (15091, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15191, 0, 'FI', 'Teksti arvovalikonvertteri 13090', 15091);
insert into tekstiryhma (id, version) values (15092, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (15192, 0, 'FI', 'Teksti valintaperuste_viite 14090', 15092);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12090, 1, false, '1', '1', 10091, 15090);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
values (13090, 1, '1', '1', '1', true, 10092, false, 15091);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (14190, 1, '1', 'syotettavanarvonkoodi_14190');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14090, 1, 'Valintaperuste_viite 14090 funktiokutsulle 10093', 'HAETTAVA_ARVO', false, 'ET', 10093, false, 2, 15092, true, false, 14190, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2090, 0, 'laskentakaava 2090 funktiokutsulle 10090', 'laskentakaava 2090 funktiokutsulle 10090', false, 'LUKUARVOFUNKTIO', 10090);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2091, 0, 'laskentakaava 2091 funktiokutsulle 10091', 'laskentakaava 2091 funktiokutsulle 10091', false, 'LUKUARVOFUNKTIO', 10091);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2092, 0, 'laskentakaava 2092 funktiokutsulle 10092', 'laskentakaava 2092 funktiokutsulle 10092', false, 'LUKUARVOFUNKTIO', 10092);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2093, 0, 'laskentakaava 2093 funktiokutsulle 10093', 'laskentakaava 2093 funktiokutsulle 10093', false, 'LUKUARVOFUNKTIO', 10093);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10100, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10101, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10102, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11101, 1, 'syoteparametrin arvo 11101 funktiokutsulle 10101', 'avain', 10101);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11102, 1, 'syoteparametrin arvo 11102 funktiokutsulle 10102', 'avain', 10102);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2100, 0, 'laskentakaava 2100 funktiokutsulle 10100', 'laskentakaava 2100 funktiokutsulle 10100', false, 'TOTUUSARVOFUNKTIO', 10100);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16100, 1, 1, 10101, null, 10100);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16101, 1, 1, 10102, null, 10100);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10110, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10111, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10112, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11112, 1, 'syoteparametrin arvo 11112 funktiokutsulle 10112', 'avain', 10112);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2110, 0, 'laskentakaava 2110 funktiokutsulle 10110', 'laskentakaava 2110 funktiokutsulle 10110', false, 'TOTUUSARVOFUNKTIO', 10110);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2111, 0, 'laskentakaava 2111 funktiokutsulle 10111', 'laskentakaava 2111 funktiokutsulle 10111', false, 'LUKUARVOFUNKTIO', 10111);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16110, 1, 1, null, 2111, 10110);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16111, 1, 1, 10112, null, 10111);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10120, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10121, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10122, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10123, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11120, 1, 'syoteparametrin arvo 11120 funktiokutsulle 10120', 'avain', 10120);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11121, 1, 'syoteparametrin arvo 11121 funktiokutsulle 10121', 'avain', 10121);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11122, 1, 'syoteparametrin arvo 11122 funktiokutsulle 10122', 'avain', 10122);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11123, 1, 'syoteparametrin arvo 11123 funktiokutsulle 10123', 'avain', 10123);

insert into tekstiryhma (id, version) values (15120, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151120, 0, 'FI', 'Teksti arvokonvertteri 12120', 15120);
insert into tekstiryhma (id, version) values (15121, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151121, 0, 'FI', 'Teksti arvovalikonvertteri 13120', 15121);
insert into tekstiryhma (id, version) values (15122, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151122, 0, 'FI', 'Teksti valintaperuste_viite 14120', 15122);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12120, 1, false, '1', '1', 10121, 15120);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
values (13120, 1, '1', '1', '1', true, 10122, false, 15121);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (141120, 1, '1', 'syotettavanarvonkoodi_141120');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14120, 1, 'Valintaperuste_viite 14120 funktiokutsulle 10123', 'HAETTAVA_ARVO', false, 'ET', 10123, false, 2, 15122, true, false, 141120, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2120, 0, 'laskentakaava 2120 funktiokutsulle 10120', 'laskentakaava 2120 funktiokutsulle 10120', false, 'LUKUARVOFUNKTIO', 10120);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2121, 0, 'laskentakaava 2121 funktiokutsulle 10121', 'laskentakaava 2121 funktiokutsulle 10121', false, 'LUKUARVOFUNKTIO', 10121);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2122, 0, 'laskentakaava 2122 funktiokutsulle 10122', 'laskentakaava 2122 funktiokutsulle 10122', false, 'LUKUARVOFUNKTIO', 10122);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2123, 0, 'laskentakaava 2123 funktiokutsulle 10123', 'laskentakaava 2123 funktiokutsulle 10123', false, 'LUKUARVOFUNKTIO', 10123);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10130, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10131, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10132, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11131, 1, 'syoteparametrin arvo 11131 funktiokutsulle 10131', 'avain', 10131);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11132, 1, 'syoteparametrin arvo 11132 funktiokutsulle 10132', 'avain', 10132);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2130, 0, 'laskentakaava 2130 funktiokutsulle 10130', 'laskentakaava 2130 funktiokutsulle 10130', false, 'TOTUUSARVOFUNKTIO', 10130);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16130, 1, 1, 10131, null, 10130);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16131, 1, 1, 10132, null, 10130);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10140, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10141, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10142, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11142, 1, 'syoteparametrin arvo 11142 funktiokutsulle 10142', 'avain', 10142);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2140, 0, 'laskentakaava 2140 funktiokutsulle 10140', 'laskentakaava 2140 funktiokutsulle 10140', false, 'TOTUUSARVOFUNKTIO', 10140);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2141, 0, 'laskentakaava 2141 funktiokutsulle 10141', 'laskentakaava 2141 funktiokutsulle 10141', false, 'LUKUARVOFUNKTIO', 10141);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16140, 1, 1, null, 2141, 10140);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16141, 1, 1, 10142, null, 10141);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10150, 1, 'KESKIARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10151, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10152, 1, 'KONVERTOILUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10153, 1, 'HAEYOARVOSANA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11150, 1, 'syoteparametrin arvo 11150 funktiokutsulle 10150', 'avain', 10150);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11151, 1, 'syoteparametrin arvo 11151 funktiokutsulle 10151', 'avain', 10151);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11152, 1, 'syoteparametrin arvo 11152 funktiokutsulle 10152', 'avain', 10152);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11153, 1, 'syoteparametrin arvo 11153 funktiokutsulle 10153', 'avain', 10153);

insert into tekstiryhma (id, version) values (15150, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151150, 0, 'FI', 'Teksti arvokonvertteri 12150', 15150);
insert into tekstiryhma (id, version) values (15151, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151151, 0, 'FI', 'Teksti arvovalikonvertteri 13150', 15151);
insert into tekstiryhma (id, version) values (15152, 0);
insert into lokalisoitu_teksti (id, version, kieli, teksti, tekstiryhma_id) values (151152, 0, 'FI', 'Teksti valintaperuste_viite 14150', 15152);

insert into arvokonvertteriparametri (id, version, hylkaysperuste, paluuarvo, arvo, funktiokutsu_id, tekstiryhma_id) values (12150, 1, false, '1', '1', 10151, 15150);
insert into arvovalikonvertteriparametri (id, version, paluuarvo, maxvalue, minvalue, palauta_haettu_arvo, funktiokutsu_id, hylkaysperuste, tekstiryhma_id)
values (13150, 1, '1', '1', '1', true, 10152, false, 15151);
insert into syotettavanarvonkoodi (id, version, arvo, uri) values (141150, 1, '1', 'syotettavanarvonkoodi_141150');
insert into valintaperuste_viite (id, version, kuvaus, lahde, on_pakollinen, tunniste, funktiokutsu_id, epasuora_viittaus, indeksi,
                                  tekstiryhma_id, vaatii_osallistumisen, syotettavissa_kaikille, syotettavanarvontyyppi_id, tilastoidaan)
values (14150, 1, 'Valintaperuste_viite 14150 funktiokutsulle 10153', 'HAETTAVA_ARVO', false, 'ET', 10153, false, 2, 15152, true, false, 141150, false);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2150, 0, 'laskentakaava 2150 funktiokutsulle 10150', 'laskentakaava 2150 funktiokutsulle 10150', false, 'LUKUARVOFUNKTIO', 10150);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2151, 0, 'laskentakaava 2151 funktiokutsulle 10151', 'laskentakaava 2151 funktiokutsulle 10151', false, 'LUKUARVOFUNKTIO', 10151);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2152, 0, 'laskentakaava 2152 funktiokutsulle 10152', 'laskentakaava 2152 funktiokutsulle 10152', false, 'LUKUARVOFUNKTIO', 10152);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2153, 0, 'laskentakaava 2153 funktiokutsulle 10153', 'laskentakaava 2153 funktiokutsulle 10153', false, 'LUKUARVOFUNKTIO', 10153);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10160, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10161, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10162, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11161, 1, 'syoteparametrin arvo 11161 funktiokutsulle 10161', 'avain', 10161);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11162, 1, 'syoteparametrin arvo 11162 funktiokutsulle 10162', 'avain', 10162);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2160, 0, 'laskentakaava 2160 funktiokutsulle 10160', 'laskentakaava 2160 funktiokutsulle 10160', false, 'TOTUUSARVOFUNKTIO', 10160);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16160, 1, 1, 10161, null, 10160);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16161, 1, 1, 10162, null, 10160);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10170, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10171, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10172, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11172, 1, 'syoteparametrin arvo 11172 funktiokutsulle 10172', 'avain', 10172);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2170, 0, 'laskentakaava 2170 funktiokutsulle 10170', 'laskentakaava 2170 funktiokutsulle 10170', false, 'TOTUUSARVOFUNKTIO', 10170);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2171, 0, 'laskentakaava 2171 funktiokutsulle 10171', 'laskentakaava 2171 funktiokutsulle 10171', false, 'LUKUARVOFUNKTIO', 10171);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16170, 1, 1, null, 2171, 10170);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16171, 1, 1, 10172, null, 10171);


insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10180, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10181, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10182, 1, 'MAKSIMI', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10183, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11183, 1, 'syoteparametrin arvo 11183 funktiokutsulle 10183', 'avain', 10183);

insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2180, 0, 'laskentakaava 2180 funktiokutsulle 10180', 'laskentakaava 2180 funktiokutsulle 10180', false, 'TOTUUSARVOFUNKTIO', 10180);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16180, 1, 1, 10181, null, 10180);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16181, 1, 1, 10182, null, 10181);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16182, 1, 1, 10183, null, 10182);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10190, 1, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10191, 1, 'SUMMA', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (10192, 1, 'SUMMA', false, false);

insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (11192, 1, 'syoteparametrin arvo 11192 funktiokutsulle 10192', 'avain', 10192);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2190, 0, 'laskentakaava 2190 funktiokutsulle 10190', 'laskentakaava 2190 funktiokutsulle 10190', false, 'TOTUUSARVOFUNKTIO', 10190);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2191, 0, 'laskentakaava 2191 funktiokutsulle 10191', 'laskentakaava 2191 funktiokutsulle 10191', false, 'LUKUARVOFUNKTIO', 10191);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (2192, 0, 'laskentakaava 2192 funktiokutsulle 10192', 'laskentakaava 2192 funktiokutsulle 10192', false, 'LUKUARVOFUNKTIO', 10192);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16190, 1, 1, null, 2191, 10190);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (16191, 1, 1, null, 2192, 10190);



insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1000, null, 1, true, 'järjestyskriteeri 1000 jonolle 100', '111111112-1', 2000, 100);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1001, 1000, 1, true, 'järjestyskriteeri 1001 jonolle 100', '111111112-2', 2001, 100);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1002, 1001, 1, true, 'järjestyskriteeri 1002 jonolle 100', '111111112-3', 2002, 100);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1003, 1002, 1, true, 'järjestyskriteeri 1003 jonolle 100', '111111112-4', 2003, 100);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1010, null, 1, true, 'järjestyskriteeri 1010 jonolle 102', '111111112-5', 2010, 102);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1011, 1010, 1, true, 'järjestyskriteeri 1011 jonolle 102', '111111112-6', 2020, 102);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1012, 1011, 1, true, 'järjestyskriteeri 1012 jonolle 102', '111111112-7', 2030, 102);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1013, 1012, 1, true, 'järjestyskriteeri 1013 jonolle 102', '111111112-8', 2031, 102);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1020, null, 1, true, 'järjestyskriteeri 1020 jonolle 200', '111111112-9', 2032, 200);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1021, 1020, 1, true, 'järjestyskriteeri 1021 jonolle 200', '111111112-10', 2033, 200);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1022, 1021, 1, true, 'järjestyskriteeri 1022 jonolle 200', '111111112-11', 2040, 200);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1023, 1022, 1, true, 'järjestyskriteeri 1023 jonolle 200', '111111112-12', 2050, 200);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1030, null, 1, true, 'järjestyskriteeri 1030 jonolle 202', '111111112-13', 2060, 202);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1031, 1030, 1, true, 'järjestyskriteeri 1031 jonolle 202', '111111112-14', 2061, 202);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1032, 1031, 1, true, 'järjestyskriteeri 1032 jonolle 202', '111111112-15', 2062, 202);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1033, 1032, 1, true, 'järjestyskriteeri 1033 jonolle 202', '111111112-16', 2063, 202);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1040, null, 1, true, 'järjestyskriteeri 1040 jonolle 300', '111111112-17', 2070, 300);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1041, 1040, 1, true, 'järjestyskriteeri 1041 jonolle 300', '111111112-18', 2080, 300);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1042, 1041, 1, true, 'järjestyskriteeri 1042 jonolle 300', '111111112-19', 2090, 300);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1043, 1042, 1, true, 'järjestyskriteeri 1043 jonolle 300', '111111112-20', 2091, 300);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1050, null, 1, true, 'järjestyskriteeri 1050 jonolle 302', '111111112-21', 2092, 302);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1051, 1050, 1, true, 'järjestyskriteeri 1051 jonolle 302', '111111112-22', 2093, 302);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1052, 1051, 1, true, 'järjestyskriteeri 1052 jonolle 302', '111111112-23', 2100, 302);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1053, 1052, 1, true, 'järjestyskriteeri 1053 jonolle 302', '111111112-24', 2110, 302);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1060, null, 1, true, 'järjestyskriteeri 1060 jonolle 400', '111111112-25', 2120, 400);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1061, 1060, 1, true, 'järjestyskriteeri 1061 jonolle 400', '111111112-26', 2121, 400);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1062, 1061, 1, true, 'järjestyskriteeri 1062 jonolle 400', '111111112-27', 2122, 400);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1063, 1062, 1, true, 'järjestyskriteeri 1063 jonolle 400', '111111112-28', 2123, 400);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1070, null, 1, true, 'järjestyskriteeri 1070 jonolle 402', '111111112-29', 2130, 402);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1071, 1070, 1, true, 'järjestyskriteeri 1071 jonolle 402', '111111112-30', 2140, 402);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1072, 1071, 1, true, 'järjestyskriteeri 1072 jonolle 402', '111111112-31', 2150, 402);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1073, 1072, 1, true, 'järjestyskriteeri 1073 jonolle 402', '111111112-32', 2152, 402);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1080, null, 1, true, 'järjestyskriteeri 1080 jonolle 500', '111111112-33', 2153, 500);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1081, 1080, 1, true, 'järjestyskriteeri 1081 jonolle 500', '111111112-34', 2160, 500);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1082, 1081, 1, true, 'järjestyskriteeri 1082 jonolle 500', '111111112-35', 2170, 500);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1083, 1082, 1, true, 'järjestyskriteeri 1083 jonolle 500', '111111112-36', 2180, 500);
insert into jarjestyskriteeri (id, edellinen_jarjestyskriteeri_id, version, aktiivinen, metatiedot, oid, laskentakaava_id, valintatapajono_id)
values (1090, null, 1, true, 'järjestyskriteeri 1090 jonolle 502', '111111112-37', 2190, 502);



insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20000, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20001, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20002, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21000, 1, 'syoteparametrin arvo 21000 funktiokutsulle 20002', 'avain2', 20002);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3000, 6, 'laskentakaava 3000 funktiokutsulle 20000', 'laskentakaava 3000 funktiokutsulle 20000', false, 'TOTUUSARVOFUNKTIO', 20000);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22000, 7, 2, 20001, null, 20000);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22001, 7, 2, 20002, null, 20001);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20010, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20011, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20012, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21010, 1, 'syoteparametrin arvo 21010 funktiokutsulle 20012', 'avain2', 20012);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3010, 6, 'laskentakaava 3010 funktiokutsulle 20010', 'laskentakaava 3010 funktiokutsulle 20010', false, 'TOTUUSARVOFUNKTIO', 20010);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22010, 7, 2, 20011, null, 20010);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22011, 7, 2, 20012, null, 20011);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20020, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20021, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20022, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21020, 1, 'syoteparametrin arvo 21020 funktiokutsulle 20022', 'avain2', 20022);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3020, 6, 'laskentakaava 3020 funktiokutsulle 20020', 'laskentakaava 3020 funktiokutsulle 20020', false, 'TOTUUSARVOFUNKTIO', 20020);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22020, 7, 2, 20021, null, 20020);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22021, 7, 2, 20022, null, 20021);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20030, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20031, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20032, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21030, 1, 'syoteparametrin arvo 21030 funktiokutsulle 20032', 'avain2', 20032);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3030, 6, 'laskentakaava 3030 funktiokutsulle 20030', 'laskentakaava 3030 funktiokutsulle 20030', false, 'TOTUUSARVOFUNKTIO', 20030);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22030, 7, 2, 20031, null, 20030);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22031, 7, 2, 20032, null, 20031);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20040, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20041, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20042, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21040, 1, 'syoteparametrin arvo 21040 funktiokutsulle 20042', 'avain2', 20042);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3040, 6, 'laskentakaava 3040 funktiokutsulle 20040', 'laskentakaava 3040 funktiokutsulle 20040', false, 'TOTUUSARVOFUNKTIO', 20040);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22040, 7, 2, 20041, null, 20040);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22041, 7, 2, 20042, null, 20041);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20050, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20051, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20052, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21050, 1, 'syoteparametrin arvo 21050 funktiokutsulle 20052', 'avain2', 20052);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3050, 6, 'laskentakaava 3050 funktiokutsulle 20050', 'laskentakaava 3050 funktiokutsulle 20050', false, 'TOTUUSARVOFUNKTIO', 20050);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22050, 7, 2, 20051, null, 20050);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22051, 7, 2, 20052, null, 20051);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20060, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20061, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20062, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21060, 1, 'syoteparametrin arvo 21060 funktiokutsulle 20062', 'avain2', 20062);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3060, 6, 'laskentakaava 3060 funktiokutsulle 20060', 'laskentakaava 3060 funktiokutsulle 20060', false, 'TOTUUSARVOFUNKTIO', 20060);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22060, 7, 2, 20061, null, 20060);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22061, 7, 2, 20062, null, 20061);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20070, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20071, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20072, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21070, 1, 'syoteparametrin arvo 21070 funktiokutsulle 20072', 'avain2', 20072);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3070, 6, 'laskentakaava 3070 funktiokutsulle 20070', 'laskentakaava 3070 funktiokutsulle 20070', false, 'TOTUUSARVOFUNKTIO', 20070);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22070, 7, 2, 20071, null, 20070);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22071, 7, 2, 20072, null, 20071);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20080, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20081, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20082, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21080, 1, 'syoteparametrin arvo 21080 funktiokutsulle 20082', 'avain2', 20082);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3080, 6, 'laskentakaava 3080 funktiokutsulle 20080', 'laskentakaava 3080 funktiokutsulle 20080', false, 'TOTUUSARVOFUNKTIO', 20080);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22080, 7, 2, 20081, null, 20080);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22081, 7, 2, 20082, null, 20081);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20090, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20091, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20092, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21090, 1, 'syoteparametrin arvo 21090 funktiokutsulle 20092', 'avain2', 20092);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3090, 6, 'laskentakaava 3090 funktiokutsulle 20090', 'laskentakaava 3090 funktiokutsulle 20090', false, 'TOTUUSARVOFUNKTIO', 20090);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22090, 7, 2, 20091, null, 20090);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22091, 7, 2, 20092, null, 20091);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20100, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20101, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20102, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21100, 1, 'syoteparametrin arvo 21100 funktiokutsulle 20102', 'avain2', 20102);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3100, 6, 'laskentakaava 3100 funktiokutsulle 20100', 'laskentakaava 3100 funktiokutsulle 20100', false, 'TOTUUSARVOFUNKTIO', 20100);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22100, 7, 2, 20101, null, 20100);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22101, 7, 2, 20102, null, 20101);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20110, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20111, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20112, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21110, 1, 'syoteparametrin arvo 21110 funktiokutsulle 20112', 'avain2', 20112);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3110, 6, 'laskentakaava 3110 funktiokutsulle 20110', 'laskentakaava 3110 funktiokutsulle 20110', false, 'TOTUUSARVOFUNKTIO', 20110);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22110, 7, 2, 20111, null, 20110);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22111, 7, 2, 20112, null, 20111);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20120, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20121, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20122, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21120, 1, 'syoteparametrin arvo 21120 funktiokutsulle 20122', 'avain2', 20122);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3120, 6, 'laskentakaava 3120 funktiokutsulle 20120', 'laskentakaava 3120 funktiokutsulle 20120', false, 'TOTUUSARVOFUNKTIO', 20120);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22120, 7, 2, 20121, null, 20120);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22121, 7, 2, 20122, null, 20121);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20130, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20131, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20132, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21130, 1, 'syoteparametrin arvo 21130 funktiokutsulle 20132', 'avain2', 20132);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3130, 6, 'laskentakaava 3130 funktiokutsulle 20130', 'laskentakaava 3130 funktiokutsulle 20130', false, 'TOTUUSARVOFUNKTIO', 20130);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22130, 7, 2, 20131, null, 20130);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22131, 7, 2, 20132, null, 20131);

insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20140, 5, 'TOTUUSARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20141, 5, 'LUKUARVO', false, false);
insert into funktiokutsu (id, version, funktionimi, tallenna_tulos, oma_opintopolku) values (20142, 5, 'MINIMI', false, false);
insert into syoteparametri (id, version, arvo, avain, funktiokutsu_id) values (21140, 1, 'syoteparametrin arvo 21140 funktiokutsulle 20142', 'avain2', 20142);
insert into laskentakaava (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu_id)
values (3140, 6, 'laskentakaava 3140 funktiokutsulle 20140', 'laskentakaava 3140 funktiokutsulle 20140', false, 'TOTUUSARVOFUNKTIO', 20140);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22140, 7, 2, 20141, null, 20140);
insert into funktioargumentti (id, version, indeksi, funktiokutsuchild_id, laskentakaavachild_id, funktiokutsuparent_id) values (22141, 7, 2, 20142, null, 20141);



insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4000, 0, true, 'valintakoe 4000', 'valintakoe 4000', '111111113-1', 'valintakoe 4000', 3000, 11, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4001, 0, true, 'valintakoe 4001', 'valintakoe 4001', '111111113-2', 'valintakoe 4001', 3010, 11, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4002, 0, true, 'valintakoe 4002', 'valintakoe 4002', '111111113-3', 'valintakoe 4002', 3020, 11, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4003, 0, true, 'valintakoe 4003', 'valintakoe 4003', '111111113-4', 'valintakoe 4003', 3030, 21, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4004, 0, true, 'valintakoe 4004', 'valintakoe 4004', '111111113-5', 'valintakoe 4004', 3040, 21, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4005, 0, true, 'valintakoe 4005', 'valintakoe 4005', '111111113-6', 'valintakoe 4005', 3050, 21, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4006, 0, true, 'valintakoe 4006', 'valintakoe 4006', '111111113-7', 'valintakoe 4006', 3060, 31, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4007, 0, true, 'valintakoe 4007', 'valintakoe 4007', '111111113-8', 'valintakoe 4007', 3070, 31, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4008, 0, true, 'valintakoe 4008', 'valintakoe 4008', '111111113-9', 'valintakoe 4008', 3080, 31, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4009, 0, true, 'valintakoe 4009', 'valintakoe 4009', '111111113-10', 'valintakoe 4009', 3090, 41, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4010, 0, true, 'valintakoe 4010', 'valintakoe 4010', '111111113-11', 'valintakoe 4010', 3100, 41, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4011, 0, true, 'valintakoe 4011', 'valintakoe 4011', '111111113-12', 'valintakoe 4011', 3110, 41, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4012, 0, true, 'valintakoe 4012', 'valintakoe 4012', '111111113-13', 'valintakoe 4012', 3120, 51, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4013, 0, true, 'valintakoe 4013', 'valintakoe 4013', '111111113-14', 'valintakoe 4013', 3130, 51, false, false, 'YLIN_TOIVE');
insert into valintakoe (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki, kutsun_kohde)
values (4014, 0, true, 'valintakoe 4014', 'valintakoe 4014', '111111113-15', 'valintakoe 4014', 3140, 51, false, false, 'YLIN_TOIVE');



insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5000, 0, '1.0', 'hakukohteen_valintaperuste 5000', 'hakukohteen_valintaperuste 5000', 1);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5001, 0, '1.0', 'hakukohteen_valintaperuste 5001', 'hakukohteen_valintaperuste 5001', 1);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5002, 0, '1.0', 'hakukohteen_valintaperuste 5002', 'hakukohteen_valintaperuste 5002', 1);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5003, 0, '1.0', 'hakukohteen_valintaperuste 5003', 'hakukohteen_valintaperuste 5003', 1);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5004, 0, '1.0', 'hakukohteen_valintaperuste 5004', 'hakukohteen_valintaperuste 5004', 1);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5005, 0, '1.0', 'hakukohteen_valintaperuste 5005', 'hakukohteen_valintaperuste 5005', 2);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5006, 0, '1.0', 'hakukohteen_valintaperuste 5006', 'hakukohteen_valintaperuste 5006', 2);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5007, 0, '1.0', 'hakukohteen_valintaperuste 5007', 'hakukohteen_valintaperuste 5007', 2);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5008, 0, '1.0', 'hakukohteen_valintaperuste 5008', 'hakukohteen_valintaperuste 5008', 2);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5009, 0, '1.0', 'hakukohteen_valintaperuste 5009', 'hakukohteen_valintaperuste 5009', 2);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5010, 0, '1.0', 'hakukohteen_valintaperuste 5010', 'hakukohteen_valintaperuste 5010', 3);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5011, 0, '1.0', 'hakukohteen_valintaperuste 5011', 'hakukohteen_valintaperuste 5011', 3);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5012, 0, '1.0', 'hakukohteen_valintaperuste 5012', 'hakukohteen_valintaperuste 5012', 3);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5013, 0, '1.0', 'hakukohteen_valintaperuste 5013', 'hakukohteen_valintaperuste 5013', 3);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5014, 0, '1.0', 'hakukohteen_valintaperuste 5014', 'hakukohteen_valintaperuste 5014', 3);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5015, 0, '1.0', 'hakukohteen_valintaperuste 5015', 'hakukohteen_valintaperuste 5015', 4);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5016, 0, '1.0', 'hakukohteen_valintaperuste 5016', 'hakukohteen_valintaperuste 5016', 4);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5017, 0, '1.0', 'hakukohteen_valintaperuste 5017', 'hakukohteen_valintaperuste 5017', 4);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5018, 0, '1.0', 'hakukohteen_valintaperuste 5018', 'hakukohteen_valintaperuste 5018', 4);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5019, 0, '1.0', 'hakukohteen_valintaperuste 5019', 'hakukohteen_valintaperuste 5019', 4);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5020, 0, '1.0', 'hakukohteen_valintaperuste 5020', 'hakukohteen_valintaperuste 5020', 5);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5021, 0, '1.0', 'hakukohteen_valintaperuste 5021', 'hakukohteen_valintaperuste 5021', 5);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5022, 0, '1.0', 'hakukohteen_valintaperuste 5022', 'hakukohteen_valintaperuste 5022', 5);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5023, 0, '1.0', 'hakukohteen_valintaperuste 5023', 'hakukohteen_valintaperuste 5023', 5);
insert into hakukohteen_valintaperuste (id, version, arvo, kuvaus, tunniste, hakukohde_viite_id)
values (5024, 0, '1.0', 'hakukohteen_valintaperuste 5024', 'hakukohteen_valintaperuste 5024', 5);

alter table hakukohde_viite disable trigger all;
alter table hakukohteen_valintaperuste disable trigger all;
alter table valintaperuste_viite disable trigger all;
alter table syotettavanarvonkoodi disable trigger all;
alter table valinnan_vaihe disable trigger all;
alter table valintatapajono disable trigger all;
alter table jarjestyskriteeri disable trigger all;
alter table laskentakaava disable trigger all;
alter table funktiokutsu disable trigger all;
alter table funktioargumentti disable trigger all;
alter table syoteparametri disable trigger all;
alter table arvokonvertteriparametri disable trigger all;
alter table arvovalikonvertteriparametri disable trigger all;
alter table tekstiryhma disable trigger all;
alter table lokalisoitu_teksti disable trigger all;
alter table valintakoe disable trigger all;

update hakukohde_viite set last_modified = to_timestamp('2024-03-01 01:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 5;
update valinnan_vaihe set last_modified = to_timestamp('2024-03-01 03:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 52;
update valintatapajono set last_modified = to_timestamp('2024-03-01 05:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 502;
update valintatapajono set last_modified = to_timestamp('2024-03-01 05:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 402;
update jarjestyskriteeri set last_modified = to_timestamp('2024-03-01 07:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 1050;
update valintakoe set last_modified = to_timestamp('2024-03-01 09:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 4004;
update hakukohteen_valintaperuste set last_modified = to_timestamp('2024-03-01 11:01:02', 'yyyy-MM-dd HH24:mi:ss') where id = 5003;
