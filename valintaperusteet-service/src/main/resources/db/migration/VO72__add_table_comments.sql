comment on database valintaperusteet is 'Valintaperusteet';

--Arvokonvertteriparametri
COMMENT ON TABLE arvokonvertteriparametri IS 'Arvokonvertteriparametrit';
COMMENT ON COLUMN arvokonvertteriparametri.id IS 'Tunniste';
COMMENT ON COLUMN arvokonvertteriparametri.version IS 'Versio';
COMMENT ON COLUMN arvokonvertteriparametri.hylkaysperuste IS 'Onko hylkäysperuste';
COMMENT ON COLUMN arvokonvertteriparametri.paluuarvo IS 'Paluuarvo';
COMMENT ON COLUMN arvokonvertteriparametri.arvo IS 'Arvo';
COMMENT ON COLUMN arvokonvertteriparametri.funktiokutsu_id IS 'Arvokonvertteriparametriin liittyvän funktiokutsun id';
COMMENT ON COLUMN arvokonvertteriparametri.tekstiryhma_id IS 'Arvokonvertteriparametriin liittyvän tekstiryhmän id';

--Arvovalikonvertteriparametri
COMMENT ON TABLE arvovalikonvertteriparametri IS 'Arvovälikonvertteriparametrit';
COMMENT ON COLUMN arvovalikonvertteriparametri.id IS 'Tunniste';
COMMENT ON COLUMN arvovalikonvertteriparametri.version IS 'Versio';
COMMENT ON COLUMN arvovalikonvertteriparametri.paluuarvo IS 'Paluuarvo';
COMMENT ON COLUMN arvovalikonvertteriparametri.maxvalue IS 'Arvovälin suurin arvo';
COMMENT ON COLUMN arvovalikonvertteriparametri.minvalue IS 'Arvovälin pienin arvo';
COMMENT ON COLUMN arvovalikonvertteriparametri.palauta_haettu_arvo IS 'Palautetaanko haettu arvo';
COMMENT ON COLUMN arvovalikonvertteriparametri.funktiokutsu_id IS 'Arvovalikonvertteriparametriin liittyvän funktiokutsun id';
COMMENT ON COLUMN arvovalikonvertteriparametri.hylkaysperuste IS 'Onko hylkäysperuste';
COMMENT ON COLUMN arvovalikonvertteriparametri.tekstiryhma_id IS 'Arvovalikonvertteriparametriin liittyvän tekstiryhmän id';

--Funktioargumentti
COMMENT ON TABLE funktioargumentti IS 'Funktioargumentit';
COMMENT ON COLUMN funktioargumentti.id IS 'Tunniste';
COMMENT ON COLUMN funktioargumentti.version IS 'Versio';
COMMENT ON COLUMN funktioargumentti.indeksi IS 'Monesko funktiokutsuparentin lapsi tämä funktioargumentti on';
COMMENT ON COLUMN funktioargumentti.funktiokutsuchild_id IS 'Tähän funktioargumenttiin liittyvä funktiokutsu';
COMMENT ON COLUMN funktioargumentti.laskentakaavachild_id IS 'Tähän funktioargumenttiin liittyvä laskentakaava';
COMMENT ON COLUMN funktioargumentti.funktiokutsuparent_id IS 'Tämän funktioargumentin emofunktiokutsu';

--Funktiokutsu
COMMENT ON TABLE funktiokutsu IS 'Funktiokutsut';
COMMENT ON COLUMN funktiokutsu.id IS 'Tunniste';
COMMENT ON COLUMN funktiokutsu.version IS 'Versio';
COMMENT ON COLUMN funktiokutsu.funktionimi IS 'Funktion nimi';
COMMENT ON COLUMN funktiokutsu.tallenna_tulos IS 'Tallennetaanko tulos';
COMMENT ON COLUMN funktiokutsu.tulos_tunniste IS 'Tuloksen tunniste';
COMMENT ON COLUMN funktiokutsu.tulos_teksti_fi IS 'Tuloksen kuvaus suomeksi';
COMMENT ON COLUMN funktiokutsu.tulos_teksti_sv IS 'Tuloksen kuvaus ruotsiksi';
COMMENT ON COLUMN funktiokutsu.tulos_teksti_en IS 'Tuloksen kuvaus englanniksi';
COMMENT ON COLUMN funktiokutsu.oma_opintopolku IS 'Näytetäänkö tulos hakijalle omassa opintopolussa';

--Hakijaryhmä
COMMENT ON TABLE hakijaryhma IS 'Hakijaryhmät';
COMMENT ON COLUMN hakijaryhma.id IS 'Tunniste';
COMMENT ON COLUMN hakijaryhma.version IS 'Versio';
COMMENT ON COLUMN hakijaryhma.kiintio IS 'Hakijaryhmän paikkakiintiö';
COMMENT ON COLUMN hakijaryhma.kuvaus IS 'Hakijaryhmän kuvaus';
COMMENT ON COLUMN hakijaryhma.nimi IS 'Hakijaryhmän nimi';
COMMENT ON COLUMN hakijaryhma.oid IS 'Hakijaryhmän oid';
COMMENT ON COLUMN hakijaryhma.laskentakaava_id IS 'Käytettävän laskentakaavan id';
COMMENT ON COLUMN hakijaryhma.valintaryhma_id IS 'Valintaryhmän id';
COMMENT ON COLUMN hakijaryhma.tarkkakiintio IS 'Onko kiintiö tarkka';
COMMENT ON COLUMN hakijaryhma.kaytakaikki IS 'Käytetäänkö koko kiintiö';
COMMENT ON COLUMN hakijaryhma.kaytetaan_ryhmaan_kuuluvia IS 'Käytetäänkö ryhmään kuuluvia';
COMMENT ON COLUMN hakijaryhma.hakijaryhmatyyppikoodi_id IS 'Hakijaryhmän tyyppikoodin id';
COMMENT ON COLUMN hakijaryhma.master_hakijaryhma_id IS 'Emohakijaryhmän id';
COMMENT ON COLUMN hakijaryhma.edellinen_hakijaryhma_id IS 'Edellisen hakijaryhmän id';

--Hakijaryhmä_jono
COMMENT ON TABLE hakijaryhma_jono IS 'Hakijaryhmäjono';
COMMENT ON COLUMN hakijaryhma_jono.id IS 'Tunniste';
COMMENT ON COLUMN hakijaryhma_jono.version IS 'Versio';
COMMENT ON COLUMN hakijaryhma_jono.aktiivinen IS 'Onko aktiivinen';
COMMENT ON COLUMN hakijaryhma_jono.oid IS 'Hakijaryhmäjonon oid';
COMMENT ON COLUMN hakijaryhma_jono.edellinen_hakijaryhma_jono_id IS 'Edellisen hakijaryhmäjonon oid';
COMMENT ON COLUMN hakijaryhma_jono.hakijaryhma_id IS 'Hakijaryhmän id';
COMMENT ON COLUMN hakijaryhma_jono.master_hakijaryhma_jono_id IS 'Emohakijaryhmäjonon id';
COMMENT ON COLUMN hakijaryhma_jono.valintatapajono_id IS 'Valintatapajonon id';
COMMENT ON COLUMN hakijaryhma_jono.hakukohde_viite_id IS 'Hakukohdeviitteen id';
COMMENT ON COLUMN hakijaryhma_jono.tarkkakiintio IS 'Onko kiintiö tarkka';
COMMENT ON COLUMN hakijaryhma_jono.kaytakaikki IS 'Käytetäänkö koko kiintiö';
COMMENT ON COLUMN hakijaryhma_jono.kiintio IS 'Hakijaryhmäjonon kiintiö';
COMMENT ON COLUMN hakijaryhma_jono.kaytetaan_ryhmaan_kuuluvia IS 'Käytetäänkö ryhmään kuuluvia';
COMMENT ON COLUMN hakijaryhma_jono.hakijaryhmatyyppikoodi_id IS 'Hakijaryhmän tyyppikoodin id';

--Hakijaryhmatyyppikoodi
COMMENT ON TABLE hakijaryhmatyyppikoodi IS 'Hakijaryhmän tyyppikoodi';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.id IS 'Tunniste';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.version IS 'Versio';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.arvo IS 'Arvo';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.nimi_fi IS 'Nimi suomeksi';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.nimi_sv IS 'Nimi ruotsiksi';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.nimi_en IS 'Nimi englanniksi';
COMMENT ON COLUMN hakijaryhmatyyppikoodi.uri IS 'Hakijaryhmätyyppikoodin uri';

--Hakukohde_viite
COMMENT ON TABLE hakukohde_viite IS 'Hakukohdeviite';
COMMENT ON COLUMN hakukohde_viite.id IS 'Tunniste';
COMMENT ON COLUMN hakukohde_viite.version IS 'Versio';
COMMENT ON COLUMN hakukohde_viite.hakuoid IS 'Haun oid';
COMMENT ON COLUMN hakukohde_viite.nimi IS 'Hakukohdeviitteen nimi';
COMMENT ON COLUMN hakukohde_viite.oid IS 'Hakukohdeviitteen oid';
COMMENT ON COLUMN hakukohde_viite.hakukohdekoodi_id IS 'Hakukohdekoodin tunniste';
COMMENT ON COLUMN hakukohde_viite.valintaryhma_id IS 'Valintaryhmän tunniste';
COMMENT ON COLUMN hakukohde_viite.manuaalisesti_siirretty IS 'Onko siirretty manuaalisesti';
COMMENT ON COLUMN hakukohde_viite.tarjoajaoid IS 'Tarjoajan oid';
COMMENT ON COLUMN hakukohde_viite.tila IS 'Hakukohdeviitteen tila';

--Hakukohde_viite_valintakoekoodi
COMMENT ON TABLE hakukohde_viite_valintakoekoodi IS 'Yhdistetaulu hakukohde_viitteen ja valintakoekoodin välille';
COMMENT ON COLUMN hakukohde_viite_valintakoekoodi.hakukohde_viite_id IS 'Hakukohde_viitteen id';
COMMENT ON COLUMN hakukohde_viite_valintakoekoodi.valintakoekoodi_id IS 'valintakoekoodin id';

--Hakukohdekoodi
COMMENT ON TABLE hakukohdekoodi IS 'Hakukohdekoodi';
COMMENT ON COLUMN hakukohdekoodi.id IS 'Tunniste';
COMMENT ON COLUMN hakukohdekoodi.version IS 'Versio';
COMMENT ON COLUMN hakukohdekoodi.arvo IS 'Hakukohdekoodin arvo';
COMMENT ON COLUMN hakukohdekoodi.nimi_fi IS 'Hakukohdekoodin nimi suomeksi';
COMMENT ON COLUMN hakukohdekoodi.nimi_sv IS 'Hakukohdekoodin nimi ruotsiksi';
COMMENT ON COLUMN hakukohdekoodi.nimi_en IS 'Hakukohdekoodin nimi englanniksi';
COMMENT ON COLUMN hakukohdekoodi.uri IS 'Hakukohdekoodin uri';

--Hakukohteen_valintaperuste
COMMENT ON TABLE hakukohteen_valintaperuste IS 'Hakukohteen valintaperuste';
COMMENT ON COLUMN hakukohteen_valintaperuste.id IS 'Tunniste';
COMMENT ON COLUMN hakukohteen_valintaperuste.version IS 'Versio';
COMMENT ON COLUMN hakukohteen_valintaperuste.arvo IS 'Arvo';
COMMENT ON COLUMN hakukohteen_valintaperuste.kuvaus IS 'Hakukohteen valintaperusteen kuvaus';
COMMENT ON COLUMN hakukohteen_valintaperuste.tunniste IS 'Hakukohteen valintaperusteen tunniste';
COMMENT ON COLUMN hakukohteen_valintaperuste.hakukohde_viite_id IS 'Hakukohdeviitteen tunniste';

--Järjestyskriteeri
COMMENT ON TABLE jarjestyskriteeri IS 'Järjestyskriteeri';
COMMENT ON COLUMN jarjestyskriteeri.id IS 'Tunniste';
COMMENT ON COLUMN jarjestyskriteeri.version IS 'Versio';
COMMENT ON COLUMN jarjestyskriteeri.aktiivinen IS 'Onko aktiivinen';
COMMENT ON COLUMN jarjestyskriteeri.metatiedot IS 'Järjestyskriteerin metatiedot';
COMMENT ON COLUMN jarjestyskriteeri.oid IS 'Järjestyskriteerin oid';
COMMENT ON COLUMN jarjestyskriteeri.edellinen_jarjestyskriteeri_id IS 'Edellisen järjestyskriteerin tunniste';
COMMENT ON COLUMN jarjestyskriteeri.laskentakaava_id IS 'Laskentakaavan tunniste';
COMMENT ON COLUMN jarjestyskriteeri.master_jarjestyskriteeri_id IS 'Master-järjestyskriteerin tunniste';
COMMENT ON COLUMN jarjestyskriteeri.valintatapajono_id IS 'Valintatapajonon tunniste';

--Laskentakaava
COMMENT ON TABLE laskentakaava IS 'Laskentakaava';
COMMENT ON COLUMN laskentakaava.id IS 'Tunniste';
COMMENT ON COLUMN laskentakaava.version IS 'Versio';
COMMENT ON COLUMN laskentakaava.kuvaus IS 'Laskentakaavan kuvaus';
COMMENT ON COLUMN laskentakaava.nimi IS 'Laskentakaavan nimi';
COMMENT ON COLUMN laskentakaava.on_luonnos IS 'Onko luonnos';
COMMENT ON COLUMN laskentakaava.tyyppi IS 'Laskentakaavan tyyppi';
COMMENT ON COLUMN laskentakaava.funktiokutsu_id IS 'Funktiokutsun tunniste';
COMMENT ON COLUMN laskentakaava.hakukohdeviite IS 'Hakukohdeviitteen tunniste';
COMMENT ON COLUMN laskentakaava.valintaryhmaviite IS 'Valintaryhmäviite';
COMMENT ON COLUMN laskentakaava.kopio_laskentakaavasta_id IS 'Mistä laskentakaavasta tämä kaava on kopioitu';

--Lokalisoitu teksti
COMMENT ON TABLE lokalisoitu_teksti IS 'Lokalisoitu teksti';
COMMENT ON COLUMN lokalisoitu_teksti.id IS 'Tunniste';
COMMENT ON COLUMN lokalisoitu_teksti.version IS 'Versio';
COMMENT ON COLUMN lokalisoitu_teksti.kieli IS 'Mitä kieltä tekstisisältö on';
COMMENT ON COLUMN lokalisoitu_teksti.teksti IS 'Tekstisisältö';
COMMENT ON COLUMN lokalisoitu_teksti.tekstiryhma_id IS 'Tekstiryhmän id';

--Organisaatio
COMMENT ON TABLE organisaatio IS 'Organisaatio';
COMMENT ON COLUMN organisaatio.id IS 'Tunniste';
COMMENT ON COLUMN organisaatio.version IS 'Versio';
COMMENT ON COLUMN organisaatio.oid IS 'Organisaation oid';
COMMENT ON COLUMN organisaatio.parent_oid_path IS 'Emo-organisaatiorakenteen polku';

--Syoteparametri
COMMENT ON TABLE syoteparametri IS 'Syöteparametri';
COMMENT ON COLUMN syoteparametri.id IS 'Tunniste';
COMMENT ON COLUMN syoteparametri.version IS 'Versio';
COMMENT ON COLUMN syoteparametri.arvo IS 'Syöteparametrin arvo';
COMMENT ON COLUMN syoteparametri.avain IS 'Syöteparametrin avain';
COMMENT ON COLUMN syoteparametri.funktiokutsu_id IS 'Funktiokutsun tunniste';


--Syötettävä narvon koodi
COMMENT ON TABLE syotettavanarvonkoodi IS 'Syötettävän arvon koodi';
COMMENT ON COLUMN syotettavanarvonkoodi.id IS 'Tunniste';
COMMENT ON COLUMN syotettavanarvonkoodi.version IS 'Versio';
COMMENT ON COLUMN syotettavanarvonkoodi.arvo IS 'Arvo';
COMMENT ON COLUMN syotettavanarvonkoodi.nimi_fi IS 'Syötettävän arvon nimi suomeksi';
COMMENT ON COLUMN syotettavanarvonkoodi.nimi_sv IS 'Syötettävän arvon nimi ruotsiksi';
COMMENT ON COLUMN syotettavanarvonkoodi.nimi_en IS 'Syötettävän arvon nimi englanniksi';
COMMENT ON COLUMN syotettavanarvonkoodi.uri IS 'Syötettävän arvon tyyppi-uri';

--Tekstiryhmä
COMMENT ON TABLE tekstiryhma IS 'Tekstiryhmä';
COMMENT ON COLUMN tekstiryhma.id IS 'Tunniste';
COMMENT ON COLUMN tekstiryhma.version IS 'Tekstiryhmän versio';


--Valinnan_vaihe
COMMENT ON TABLE valinnan_vaihe IS 'Valinnan vaihe';
COMMENT ON COLUMN valinnan_vaihe.id IS 'Tunniste';
COMMENT ON COLUMN valinnan_vaihe.version IS 'Versio';
COMMENT ON COLUMN valinnan_vaihe.aktiivinen IS 'Onko aktiivinen';
COMMENT ON COLUMN valinnan_vaihe.kuvaus IS 'Valinnan vaiheen kuvaus';
COMMENT ON COLUMN valinnan_vaihe.nimi IS 'Valinnan vaiheen nimi';
COMMENT ON COLUMN valinnan_vaihe.oid IS 'Valinnan vaiheen oid';
COMMENT ON COLUMN valinnan_vaihe.valinnan_vaihe_tyyppi IS 'Valinnan vaiheen tyyppi';
COMMENT ON COLUMN valinnan_vaihe.edellinen_valinnan_vaihe_id IS 'Edellisen valinnan vaiheen tunniste';
COMMENT ON COLUMN valinnan_vaihe.hakukohde_viite_id IS 'Hakukohdeviitteen tunniste';
COMMENT ON COLUMN valinnan_vaihe.master_valinnan_vaihe_id IS 'Master-valinnan_vaiheen tunniste';
COMMENT ON COLUMN valinnan_vaihe.valintaryhma_id IS 'Valintaryhmän tunniste';

--Valintakoe
COMMENT ON TABLE valintakoe IS 'Valintakoe';
COMMENT ON COLUMN valintakoe.id IS 'Tunniste';
COMMENT ON COLUMN valintakoe.version IS 'Versio';
COMMENT ON COLUMN valintakoe.aktiivinen IS 'Onko aktiivinen';
COMMENT ON COLUMN valintakoe.kuvaus IS 'Valintakokeen kuvaus';
COMMENT ON COLUMN valintakoe.nimi IS 'Valintakokeen nimi';
COMMENT ON COLUMN valintakoe.oid IS 'Valintakokeen oid';
COMMENT ON COLUMN valintakoe.tunniste IS 'Valintakokeen tarkenne';
COMMENT ON COLUMN valintakoe.laskentakaava_id IS 'Laskentakaavan tunniste';
COMMENT ON COLUMN valintakoe.master_valintakoe_id IS 'Master-valintakokeen tunniste';
COMMENT ON COLUMN valintakoe.valinnan_vaihe_id IS 'Valinnan vaiheen tunniste';
COMMENT ON COLUMN valintakoe.lahetetaanko_koekutsut IS 'Lähetetäänkö koekutsut';
COMMENT ON COLUMN valintakoe.kutsutaanko_kaikki IS 'Kutsutaanko kaikki';
COMMENT ON COLUMN valintakoe.kutsuttavien_maara IS 'Kutsuttavien lukumäärä';
COMMENT ON COLUMN valintakoe.kutsun_kohde IS 'Kutsun kohde';
COMMENT ON COLUMN valintakoe.kutsun_kohde_avain IS 'Kutsun kohteen avain';

--Valintakoekoodi
COMMENT ON TABLE valintakoekoodi IS 'Valintakoekoodi';
COMMENT ON COLUMN valintakoekoodi.id IS 'Tunniste';
COMMENT ON COLUMN valintakoekoodi.version IS 'Versio';
COMMENT ON COLUMN valintakoekoodi.arvo IS 'Valintakoekoodin arvo';
COMMENT ON COLUMN valintakoekoodi.nimi_fi IS 'Valintakoekoodin nimi suomeksi';
COMMENT ON COLUMN valintakoekoodi.nimi_sv IS 'Valintakoekoodin nimi ruotsiksi';
COMMENT ON COLUMN valintakoekoodi.nimi_en IS 'Valintakoekoodin nimi englanniksi';
COMMENT ON COLUMN valintakoekoodi.uri IS 'Valintakokeen tyypin tunniste';

--Valintaperuste_viite
COMMENT ON TABLE valintaperuste_viite IS 'Valintaperusteviite';
COMMENT ON COLUMN valintaperuste_viite.id IS 'Tunniste';
COMMENT ON COLUMN valintaperuste_viite.version IS 'Versio';
COMMENT ON COLUMN valintaperuste_viite.kuvaus IS 'Valintaperusteviitteen kuvaus';
COMMENT ON COLUMN valintaperuste_viite.lahde IS 'Mistä käytettävä vertailuarvo tulee';
COMMENT ON COLUMN valintaperuste_viite.on_pakollinen IS 'Onko pakollinen';
COMMENT ON COLUMN valintaperuste_viite.tunniste IS 'Käytettävän vertailuarvon tunniste';
COMMENT ON COLUMN valintaperuste_viite.funktiokutsu_id IS 'Funktiokutsun tunniste';
COMMENT ON COLUMN valintaperuste_viite.epasuora_viittaus IS 'Onko epäsuora viittaus';
COMMENT ON COLUMN valintaperuste_viite.indeksi IS '';
COMMENT ON COLUMN valintaperuste_viite.tekstiryhma_id IS 'Tekstiryhmän tunniste';
COMMENT ON COLUMN valintaperuste_viite.vaatii_osallistumisen IS 'Vaatiiko osallistumisen';
COMMENT ON COLUMN valintaperuste_viite.syotettavissa_kaikille IS 'Onko syötettävissä kaikille';
COMMENT ON COLUMN valintaperuste_viite.syotettavanarvontyyppi_id IS 'Syötettävän arvon koodin id';
COMMENT ON COLUMN valintaperuste_viite.tilastoidaan IS 'Tilastoidaanko tulos';

--Valintaryhma
COMMENT ON TABLE valintaryhma IS 'Valintaryhmä';
COMMENT ON COLUMN valintaryhma.id IS 'Tunniste';
COMMENT ON COLUMN valintaryhma.version IS 'Versio';
COMMENT ON COLUMN valintaryhma.nimi IS 'Valintaryhmän nimi';
COMMENT ON COLUMN valintaryhma.oid IS 'Valintaryhmän oid';
COMMENT ON COLUMN valintaryhma.parent_id IS 'Emovalintaryhmän tunniste';
COMMENT ON COLUMN valintaryhma.kohdejoukko IS 'Valintaryhmän kohdejoukko';
COMMENT ON COLUMN valintaryhma.hakuoid IS 'Haun oid';
COMMENT ON COLUMN valintaryhma.hakuvuosi IS 'Hakuvuosi';
COMMENT ON COLUMN valintaryhma.vastuuorganisaatio_id IS 'Vastuuorganisaation tunniste';
COMMENT ON COLUMN valintaryhma.viimeinen_kaynnistyspaiva IS 'Viimeinen käynnistyspäivä (aikaleima)';

--Valintaryhma_hakukohdekoodi
COMMENT ON TABLE valintaryhma_hakukohdekoodi IS 'Linkitystaulu valintaryhmän ja hakukohdekoodin välillä';
COMMENT ON COLUMN valintaryhma_hakukohdekoodi.valintaryhma_id IS 'Valintaryhmän tunniste';
COMMENT ON COLUMN valintaryhma_hakukohdekoodi.hakukohdekoodi_id IS 'Hakukohdekoodin tunniste';

--Valintaryhma_organisaatio
COMMENT ON TABLE valintaryhma_organisaatio IS 'Linkitystaulu valintaryhmän ja organisaation välillä';
COMMENT ON COLUMN valintaryhma_organisaatio.valintaryhma_id IS 'Valintaryhmän tunniste';
COMMENT ON COLUMN valintaryhma_organisaatio.organisaatio_id IS 'Organisaation tunniste';

--Valintaryhma_valintakoekoodi
COMMENT ON TABLE valintaryhma_valintakoekoodi IS 'Linkitystaulu valintaryhmän ja valintakoekoodin välillä';
COMMENT ON COLUMN valintaryhma_valintakoekoodi.valintaryhma_id IS 'Valintaryhmän tunniste';
COMMENT ON COLUMN valintaryhma_valintakoekoodi.valintakoekoodi_id IS 'Valintakoekoodin tunniste';

--Valintatapajono
COMMENT ON TABLE valintatapajono IS 'Valintatapajonot';
COMMENT ON COLUMN valintatapajono.id IS 'Tunniste';
COMMENT ON COLUMN valintatapajono.version IS 'Versio';
COMMENT ON COLUMN valintatapajono.aktiivinen IS 'Onko valintatapajono aktiivinen';
COMMENT ON COLUMN valintatapajono.aloituspaikat IS 'Aloituspaikkojen lukumäärä';
COMMENT ON COLUMN valintatapajono.kuvaus IS 'Valintatapajonon kuvaus';
COMMENT ON COLUMN valintatapajono.nimi IS 'Valintatapajonon nimi';
COMMENT ON COLUMN valintatapajono.oid IS 'Valintatapajonon oid';
COMMENT ON COLUMN valintatapajono.siirretaan_sijoitteluun IS 'Siirretäänkö jono sijoitteluun';
COMMENT ON COLUMN valintatapajono.tasapistesaanto IS 'Tasapistetilanteissa käytettävä sääntö';
COMMENT ON COLUMN valintatapajono.edellinen_valintatapajono_id IS 'Edellisen valintatapajonon tunniste';
COMMENT ON COLUMN valintatapajono.master_valintatapajono_id IS 'Master-valintatapajonon tunniste';
COMMENT ON COLUMN valintatapajono.valinnan_vaihe_id IS 'Valinnanvaiheen id';
COMMENT ON COLUMN valintatapajono.ei_varasijatayttoa IS 'Onko varasijatäyttö poistettu käytöstä';
COMMENT ON COLUMN valintatapajono.poissa_oleva_taytto IS 'Käytetäänkö poissaolevaa täyttöä';
COMMENT ON COLUMN valintatapajono.varasijat IS 'Varasijojen lukumäärä';
COMMENT ON COLUMN valintatapajono.varasijan_tayttojono_id IS 'Varasijojen täyttöjonon koko';
COMMENT ON COLUMN valintatapajono.varasijoja_kaytetaan_alkaen IS 'Mistä alkaen varasijoja käytetään';
COMMENT ON COLUMN valintatapajono.varasijoja_taytetaan_asti IS 'Mihin asti varasijoja täytetään';
COMMENT ON COLUMN valintatapajono.kaytetaan_valintalaskentaa IS 'Käytetäänkö jonossa valintalaskentaa';
COMMENT ON COLUMN valintatapajono.kaikki_ehdon_tayttavat_hyvaksytaan IS 'Hyväksytäänkö kaikki ehdon täyttävät';
COMMENT ON COLUMN valintatapajono.valisijoittelu IS 'Käytetäänkö välisijoittelua';
COMMENT ON COLUMN valintatapajono.automaattinen_sijoitteluun_siirto IS 'Siirretäänkö jono automaattisesti sijoitteluun';
COMMENT ON COLUMN valintatapajono.poistetaanko_hylatyt IS 'Poistetaanko hylätyt';
COMMENT ON COLUMN valintatapajono.tyyppi IS 'Valintatapajonon tyyppi';
