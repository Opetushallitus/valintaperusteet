package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaKopiointiDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicReference;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ValintaryhmaKopiointiDAOImpl implements ValintaryhmaKopiointiDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValintaryhmaKopiointiDAOImpl.class);

  /** Suojaraja laskentakaavojen viittausketjun läpikäynnille. */
  private static final int KAAVASULKEUMAN_MAKSIMISYVYYS = 100;

  @PersistenceContext private EntityManager entityManager;

  @Override
  public String kopioiJuureen(String lahdeOid, String nimi) {
    return suorita(lahdeOid, null, nimi);
  }

  @Override
  public String kopioiVanhemmanAlle(String lahdeOid, String vanhempiOid, String nimi) {
    return suorita(lahdeOid, vanhempiOid, nimi);
  }

  private String suorita(String lahdeOid, String vanhempiOid, String nimi) {
    // Kopiointi tehdään suoraan kannassa, joten kaikki keskeneräiset muutokset on kirjoitettava
    // ensin ja persistence context tyhjennettävä lopuksi, ettei sinne jää vanhentuneita olioita.
    entityManager.flush();
    AtomicReference<String> uusiOid = new AtomicReference<>();
    entityManager
        .unwrap(Session.class)
        .doWork(connection -> uusiOid.set(kopioi(connection, lahdeOid, vanhempiOid, nimi)));
    entityManager.clear();
    return uusiOid.get();
  }

  private String kopioi(Connection c, String lahdeOid, String vanhempiOid, String nimi)
      throws SQLException {
    Long vanhempiId = vanhempiOid == null ? null : haeValintaryhmanId(c, vanhempiOid);
    luoValiaikaisetTaulut(c);
    int solmuja = varaaValintaryhmienTunnisteet(c, lahdeOid);
    if (solmuja == 0) {
      throw new IllegalStateException("Valintaryhmää " + lahdeOid + " ei löytynyt");
    }

    kopioiValintaryhmat(c, nimi, vanhempiId);
    kopioiKoodiJaOrganisaatioliitokset(c);

    if (vanhempiId == null) {
      varaaAlipuunRakenteenTunnisteet(c);
    } else {
      taytaPerintalahteet(c, vanhempiId);
    }

    varaaLaskentakaavojenTunnisteet(c);
    kopioiLaskentakaavat(c);

    if (vanhempiId == null) {
      kopioiAlipuunRakenne(c);
    } else {
      kopioiPeritytRakenteet(c);
    }

    String uusiOid = haeKopioidunJuurenOid(c);
    LOGGER.info(
        "Kopioitiin valintaryhmä {} ({} solmua) nimellä '{}' -> {}",
        lahdeOid,
        solmuja,
        nimi,
        uusiOid);
    return uusiOid;
  }

  // ------------------------------------------------------------------
  // Valmistelu
  // ------------------------------------------------------------------

  private void luoValiaikaisetTaulut(Connection c) throws SQLException {
    paivita(c, "drop table if exists kopiointi_map");
    paivita(c, "drop table if exists kopiointi_peritty");
    paivita(
        c,
        """
        create temporary table kopiointi_map (
            tyyppi   varchar(40) not null,
            kohde_id bigint      not null,
            vanha_id bigint      not null,
            uusi_id  bigint      not null,
            syvyys   integer     not null default 0,
            primary key (tyyppi, kohde_id, vanha_id)
        ) on commit drop
        """);
    paivita(
        c,
        """
        create temporary table kopiointi_peritty (
            kohde_id    bigint  not null primary key,
            lahde_vr_id bigint  not null,
            syvyys      integer not null
        ) on commit drop
        """);
  }

  private Long haeValintaryhmanId(Connection c, String oid) throws SQLException {
    try (PreparedStatement ps = c.prepareStatement("select id from valintaryhma where oid = ?")) {
      ps.setString(1, oid);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          throw new IllegalStateException("Valintaryhmää " + oid + " ei löytynyt");
        }
        return rs.getLong(1);
      }
    }
  }

  private int varaaValintaryhmienTunnisteet(Connection c, String lahdeOid) throws SQLException {
    return paivita(
        c,
        """
        insert into kopiointi_map (tyyppi, kohde_id, vanha_id, uusi_id, syvyys)
        with recursive alipuu as (
            select vr.id, 0 as syvyys from valintaryhma vr where vr.oid = ?
          union all
            select vr.id, a.syvyys + 1
              from valintaryhma vr join alipuu a on vr.parent_id = a.id
        )
        select 'valintaryhma', 0, a.id, nextval('hibernate_sequence'), a.syvyys from alipuu a
        """,
        lahdeOid);
  }

  // ------------------------------------------------------------------
  // Valintaryhmät, koodit ja organisaatiot (sama molemmille poluille)
  // ------------------------------------------------------------------

  private void kopioiValintaryhmat(Connection c, String nimi, Long vanhempiId) throws SQLException {
    paivita(
        c,
        """
        insert into valintaryhma
            (id, version, nimi, oid, parent_id, kohdejoukko, vastuuorganisaatio_id)
        select m.uusi_id,
               0,
               case when m.syvyys = 0 then ? else vr.nimi end,
               kopioinnin_uusi_oid(),
               case when m.syvyys = 0 then ? else pm.uusi_id end,
               vr.kohdejoukko,
               vr.vastuuorganisaatio_id
          from kopiointi_map m
          join valintaryhma vr on vr.id = m.vanha_id
          left join kopiointi_map pm
                 on pm.tyyppi = 'valintaryhma' and pm.kohde_id = 0
                and pm.vanha_id = vr.parent_id
         where m.tyyppi = 'valintaryhma' and m.kohde_id = 0
        """,
        nimi,
        vanhempiId);
  }

  private void kopioiKoodiJaOrganisaatioliitokset(Connection c) throws SQLException {
    for (String[] taulu :
        new String[][] {
          {"valintaryhma_organisaatio", "organisaatio_id"},
          {"valintaryhma_hakukohdekoodi", "hakukohdekoodi_id"},
          {"valintaryhma_valintakoekoodi", "valintakoekoodi_id"}
        }) {
      paivita(
          c,
          ("""
           insert into %s (valintaryhma_id, %s)
           select m.uusi_id, l.%s
             from kopiointi_map m
             join %s l on l.valintaryhma_id = m.vanha_id
            where m.tyyppi = 'valintaryhma' and m.kohde_id = 0
           """)
              .formatted(taulu[0], taulu[1], taulu[1], taulu[0]));
    }
  }

  // ------------------------------------------------------------------
  // Laskentakaavat (sama molemmille poluille)
  // ------------------------------------------------------------------

  /**
   * Kopioitavat kaavat ovat alipuun omat kaavat sekä ne joihin kopioitavat järjestyskriteerit,
   * hakijaryhmät ja valintakokeet viittaavat — ja näiden funktiokutsuista löytyvät alikaavat
   * transitiivisesti.
   */
  private void varaaLaskentakaavojenTunnisteet(Connection c) throws SQLException {
    paivita(
        c,
        """
        insert into kopiointi_map (tyyppi, kohde_id, vanha_id, uusi_id)
        select 'laskentakaava', 0, s.id, nextval('hibernate_sequence')
          from (
              select lk.id
                from laskentakaava lk
               where lk.valintaryhmaviite in
                     (select vanha_id from kopiointi_map
                       where tyyppi = 'valintaryhma' and kohde_id = 0)
            union
              select jk.laskentakaava_id
                from jarjestyskriteeri jk
               where jk.id in (select vanha_id from kopiointi_map where tyyppi = 'jarjestyskriteeri')
            union
              select hr.laskentakaava_id
                from hakijaryhma hr
               where hr.id in (select vanha_id from kopiointi_map where tyyppi = 'hakijaryhma')
            union
              select vk.laskentakaava_id
                from valintakoe vk
               where vk.id in (select vanha_id from kopiointi_map where tyyppi = 'valintakoe')
                 and vk.laskentakaava_id is not null
          ) s
        """);

    for (int kierros = 0; kierros < KAAVASULKEUMAN_MAKSIMISYVYYS; kierros++) {
      int lisatty =
          paivita(
              c,
              """
              insert into kopiointi_map (tyyppi, kohde_id, vanha_id, uusi_id)
              select 'laskentakaava', 0, v.viitattu, nextval('hibernate_sequence')
                from (
                      select distinct (viite)::text::bigint as viitattu
                        from kopiointi_map m
                        join laskentakaava lk on lk.id = m.vanha_id,
                             lateral jsonb_array_elements(
                                 jsonb_path_query_array(
                                     lk.funktiokutsu, 'strict $.**.laskentakaavaChild.id')) as viite
                       where m.tyyppi = 'laskentakaava' and m.kohde_id = 0
                     ) v
               where exists (select 1 from laskentakaava lk2 where lk2.id = v.viitattu)
                 and not exists (select 1 from kopiointi_map m2
                                  where m2.tyyppi = 'laskentakaava'
                                    and m2.kohde_id = 0
                                    and m2.vanha_id = v.viitattu)
              """);
      if (lisatty == 0) {
        return;
      }
    }
    throw new IllegalStateException(
        "Laskentakaavojen viittausketju ei sulkeutunut "
            + KAAVASULKEUMAN_MAKSIMISYVYYS
            + " kierroksessa");
  }

  /**
   * Kopioitavan joukon ulkopuolelta peritty kaava kiinnitetään kopion juureen. Juuri on kaikkien
   * kopioitujen solmujen esivanhempi, joten kaava periytyy samoille solmuille kuin ennenkin.
   */
  private void kopioiLaskentakaavat(Connection c) throws SQLException {
    paivita(
        c,
        """
        insert into laskentakaava
            (id, version, kuvaus, nimi, on_luonnos, tyyppi, funktiokutsu,
             valintaryhmaviite, hakukohdeviite, kopio_laskentakaavasta_id)
        select m.uusi_id,
               0,
               lk.kuvaus,
               lk.nimi,
               lk.on_luonnos,
               lk.tyyppi,
               kopioi_laskentakaavaviitteet(
                   lk.funktiokutsu,
                   (select coalesce(jsonb_object_agg(k.vanha_id::text, k.uusi_id), '{}'::jsonb)
                      from kopiointi_map k
                     where k.tyyppi = 'laskentakaava' and k.kohde_id = 0)),
               coalesce(vm.uusi_id,
                        (select uusi_id from kopiointi_map
                          where tyyppi = 'valintaryhma' and kohde_id = 0 and syvyys = 0)),
               null,
               lk.id
          from kopiointi_map m
          join laskentakaava lk on lk.id = m.vanha_id
          left join kopiointi_map vm
                 on vm.tyyppi = 'valintaryhma' and vm.kohde_id = 0
                and vm.vanha_id = lk.valintaryhmaviite
         where m.tyyppi = 'laskentakaava' and m.kohde_id = 0
        """);
  }

  // ------------------------------------------------------------------
  // Juureen kopiointi: alipuun oma rakenne kopioidaan sellaisenaan
  // ------------------------------------------------------------------

  private void varaaAlipuunRakenteenTunnisteet(Connection c) throws SQLException {
    varaa(
        c,
        "valinnan_vaihe",
        """
        select vv.id from valinnan_vaihe vv
         where vv.valintaryhma_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valintaryhma' and kohde_id = 0)
        """);
    varaa(
        c,
        "valintatapajono",
        """
        select j.id from valintatapajono j
         where j.valinnan_vaihe_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valinnan_vaihe')
        """);
    varaa(
        c,
        "jarjestyskriteeri",
        """
        select jk.id from jarjestyskriteeri jk
         where jk.valintatapajono_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valintatapajono')
        """);
    varaa(
        c,
        "valintakoe",
        """
        select vk.id from valintakoe vk
         where vk.valinnan_vaihe_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valinnan_vaihe')
        """);
    varaa(
        c,
        "hakijaryhma",
        """
        select hr.id from hakijaryhma hr
         where hr.valintaryhma_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valintaryhma' and kohde_id = 0)
        """);
    varaa(
        c,
        "hakijaryhma_jono",
        """
        select hj.id from hakijaryhma_jono hj
         where hj.valintatapajono_id in
               (select vanha_id from kopiointi_map where tyyppi = 'valintatapajono')
        """);
  }

  private void varaa(Connection c, String tyyppi, String lahdeKysely) throws SQLException {
    paivita(
        c,
        ("""
         insert into kopiointi_map (tyyppi, kohde_id, vanha_id, uusi_id)
         select '%s', 0, s.id, nextval('hibernate_sequence') from (%s) s
         """)
            .formatted(tyyppi, lahdeKysely));
  }

  private void kopioiAlipuunRakenne(Connection c) throws SQLException {
    paivita(
        c,
        """
        insert into valinnan_vaihe
            (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi,
             valintaryhma_id, hakukohde_viite_id,
             master_valinnan_vaihe_id, edellinen_valinnan_vaihe_id)
        select m.uusi_id, 0, vv.aktiivinen, vv.kuvaus, vv.nimi, kopioinnin_uusi_oid(),
               vv.valinnan_vaihe_tyyppi, vrm.uusi_id, null, mm.uusi_id, em.uusi_id
          from kopiointi_map m
          join valinnan_vaihe vv on vv.id = m.vanha_id
          join kopiointi_map vrm
                 on vrm.tyyppi = 'valintaryhma' and vrm.kohde_id = 0
                and vrm.vanha_id = vv.valintaryhma_id
          left join kopiointi_map mm
                 on mm.tyyppi = 'valinnan_vaihe' and mm.kohde_id = 0
                and mm.vanha_id = vv.master_valinnan_vaihe_id
          left join kopiointi_map em
                 on em.tyyppi = 'valinnan_vaihe' and em.kohde_id = 0
                and em.vanha_id = vv.edellinen_valinnan_vaihe_id
         where m.tyyppi = 'valinnan_vaihe'
        """);

    paivita(c, jonoInsert(true));
    paivita(c, kriteeriInsert(true));
    paivita(c, valintakoeInsert(true));

    paivita(
        c,
        """
        insert into hakijaryhma
            (id, version, kiintio, kuvaus, nimi, oid, laskentakaava_id, valintaryhma_id,
             tarkkakiintio, kaytakaikki, kaytetaan_ryhmaan_kuuluvia, hakijaryhmatyyppikoodi_id,
             master_hakijaryhma_id, edellinen_hakijaryhma_id)
        select m.uusi_id, 0, hr.kiintio, hr.kuvaus, hr.nimi, kopioinnin_uusi_oid(),
               coalesce(lkm.uusi_id, hr.laskentakaava_id), vrm.uusi_id,
               hr.tarkkakiintio, hr.kaytakaikki, hr.kaytetaan_ryhmaan_kuuluvia,
               hr.hakijaryhmatyyppikoodi_id, mm.uusi_id, em.uusi_id
          from kopiointi_map m
          join hakijaryhma hr on hr.id = m.vanha_id
          join kopiointi_map vrm
                 on vrm.tyyppi = 'valintaryhma' and vrm.kohde_id = 0
                and vrm.vanha_id = hr.valintaryhma_id
          left join kopiointi_map lkm
                 on lkm.tyyppi = 'laskentakaava' and lkm.kohde_id = 0
                and lkm.vanha_id = hr.laskentakaava_id
          left join kopiointi_map mm
                 on mm.tyyppi = 'hakijaryhma' and mm.kohde_id = 0
                and mm.vanha_id = hr.master_hakijaryhma_id
          left join kopiointi_map em
                 on em.tyyppi = 'hakijaryhma' and em.kohde_id = 0
                and em.vanha_id = hr.edellinen_hakijaryhma_id
         where m.tyyppi = 'hakijaryhma'
        """);

    paivita(c, hakijaryhmaJonoInsert(true));
  }

  // ------------------------------------------------------------------
  // Vanhemman alle kopiointi: rakenne peritään kohdevanhemmalta taso kerrallaan
  // ------------------------------------------------------------------

  private void taytaPerintalahteet(Connection c, Long vanhempiId) throws SQLException {
    paivita(
        c,
        """
        insert into kopiointi_peritty (kohde_id, lahde_vr_id, syvyys)
        select m.uusi_id,
               case when m.syvyys = 0 then ? else pm.uusi_id end,
               m.syvyys
          from kopiointi_map m
          join valintaryhma vr on vr.id = m.vanha_id
          left join kopiointi_map pm
                 on pm.tyyppi = 'valintaryhma' and pm.kohde_id = 0
                and pm.vanha_id = vr.parent_id
         where m.tyyppi = 'valintaryhma' and m.kohde_id = 0
        """,
        vanhempiId);
  }

  private void kopioiPeritytRakenteet(Connection c) throws SQLException {
    int maksimisyvyys = haeMaksimisyvyys(c);
    for (int syvyys = 0; syvyys <= maksimisyvyys; syvyys++) {
      kopioiTasonPeritytRakenteet(c, syvyys);
    }
  }

  private int haeMaksimisyvyys(Connection c) throws SQLException {
    try (PreparedStatement ps =
            c.prepareStatement("select coalesce(max(syvyys), -1) from kopiointi_peritty");
        ResultSet rs = ps.executeQuery()) {
      rs.next();
      return rs.getInt(1);
    }
  }

  private void kopioiTasonPeritytRakenteet(Connection c, int syvyys) throws SQLException {
    varaaPeritty(
        c,
        "valinnan_vaihe",
        """
        select p.kohde_id, vv.id
          from kopiointi_peritty p
          join valinnan_vaihe vv on vv.valintaryhma_id = p.lahde_vr_id
         where p.syvyys = %d
        """,
        syvyys);
    paivita(
        c,
        """
        insert into valinnan_vaihe
            (id, version, aktiivinen, kuvaus, nimi, oid, valinnan_vaihe_tyyppi,
             valintaryhma_id, hakukohde_viite_id,
             master_valinnan_vaihe_id, edellinen_valinnan_vaihe_id)
        select m.uusi_id, 0, vv.aktiivinen, vv.kuvaus, vv.nimi, kopioinnin_uusi_oid(),
               vv.valinnan_vaihe_tyyppi, m.kohde_id, null, vv.id, em.uusi_id
          from kopiointi_map m
          join valinnan_vaihe vv on vv.id = m.vanha_id
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = ?
          left join kopiointi_map em
                 on em.tyyppi = 'valinnan_vaihe' and em.kohde_id = m.kohde_id
                and em.vanha_id = vv.edellinen_valinnan_vaihe_id
         where m.tyyppi = 'valinnan_vaihe'
        """,
        syvyys);

    varaaPeritty(
        c,
        "valintatapajono",
        """
        select m.kohde_id, j.id
          from kopiointi_map m
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = %d
          join valintatapajono j on j.valinnan_vaihe_id = m.vanha_id
         where m.tyyppi = 'valinnan_vaihe'
        """,
        syvyys);
    paivita(c, jonoInsert(false), syvyys);

    varaaPeritty(
        c,
        "jarjestyskriteeri",
        """
        select m.kohde_id, jk.id
          from kopiointi_map m
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = %d
          join jarjestyskriteeri jk on jk.valintatapajono_id = m.vanha_id
         where m.tyyppi = 'valintatapajono'
        """,
        syvyys);
    paivita(c, kriteeriInsert(false), syvyys);

    varaaPeritty(
        c,
        "valintakoe",
        """
        select m.kohde_id, vk.id
          from kopiointi_map m
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = %d
          join valintakoe vk on vk.valinnan_vaihe_id = m.vanha_id
         where m.tyyppi = 'valinnan_vaihe'
        """,
        syvyys);
    paivita(c, valintakoeInsert(false), syvyys);

    varaaPeritty(
        c,
        "hakijaryhma",
        """
        select p.kohde_id, hr.id
          from kopiointi_peritty p
          join hakijaryhma hr on hr.valintaryhma_id = p.lahde_vr_id
         where p.syvyys = %d
        """,
        syvyys);
    paivita(
        c,
        """
        insert into hakijaryhma
            (id, version, kiintio, kuvaus, nimi, oid, laskentakaava_id, valintaryhma_id,
             tarkkakiintio, kaytakaikki, kaytetaan_ryhmaan_kuuluvia, hakijaryhmatyyppikoodi_id,
             master_hakijaryhma_id, edellinen_hakijaryhma_id)
        select m.uusi_id, 0, hr.kiintio, hr.kuvaus, hr.nimi, kopioinnin_uusi_oid(),
               coalesce(lkm.uusi_id, hr.laskentakaava_id), m.kohde_id,
               hr.tarkkakiintio, hr.kaytakaikki, hr.kaytetaan_ryhmaan_kuuluvia,
               hr.hakijaryhmatyyppikoodi_id, hr.id, em.uusi_id
          from kopiointi_map m
          join hakijaryhma hr on hr.id = m.vanha_id
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = ?
          left join kopiointi_map lkm
                 on lkm.tyyppi = 'laskentakaava' and lkm.kohde_id = 0
                and lkm.vanha_id = hr.laskentakaava_id
          left join kopiointi_map em
                 on em.tyyppi = 'hakijaryhma' and em.kohde_id = m.kohde_id
                and em.vanha_id = hr.edellinen_hakijaryhma_id
         where m.tyyppi = 'hakijaryhma'
        """,
        syvyys);

    varaaPeritty(
        c,
        "hakijaryhma_jono",
        """
        select m.kohde_id, hj.id
          from kopiointi_map m
          join kopiointi_peritty p on p.kohde_id = m.kohde_id and p.syvyys = %d
          join hakijaryhma_jono hj on hj.valintatapajono_id = m.vanha_id
         where m.tyyppi = 'valintatapajono'
        """,
        syvyys);
    paivita(c, hakijaryhmaJonoInsert(false), syvyys);
  }

  private void varaaPeritty(Connection c, String tyyppi, String lahdeKysely, int syvyys)
      throws SQLException {
    paivita(
        c,
        ("""
         insert into kopiointi_map (tyyppi, kohde_id, vanha_id, uusi_id)
         select '%s', s.kohde_id, s.id, nextval('hibernate_sequence') from (%s) s
         """)
            .formatted(tyyppi, lahdeKysely.formatted(syvyys)));
  }

  // ------------------------------------------------------------------
  // Yhteiset insert-lauseet
  //
  // Juureen kopioitaessa master-viite osoitetaan kopioon ja koko joukko rajataan
  // mäppäystaulun kohde_id = 0 -riveihin. Vanhemman alle kopioitaessa master on lähderivi
  // itse ja joukko rajataan käsiteltävään perintätasoon.
  // ------------------------------------------------------------------

  private String jonoInsert(boolean juureen) {
    return ("""
        insert into valintatapajono
            (id, version, aktiivinen, aloituspaikat, kuvaus, nimi, oid, siirretaan_sijoitteluun,
             tasapistesaanto, ei_varasijatayttoa, poissa_oleva_taytto, varasijat,
             varasijoja_kaytetaan_alkaen, varasijoja_taytetaan_asti, kaytetaan_valintalaskentaa,
             kaikki_ehdon_tayttavat_hyvaksytaan, valisijoittelu, automaattinen_sijoitteluun_siirto,
             poistetaanko_hylatyt, tyyppi, merkitse_myoh_auto, varasijan_tayttojono_id,
             ei_lasketa_paivamaaran_jalkeen,
             valinnan_vaihe_id, master_valintatapajono_id, edellinen_valintatapajono_id)
        select m.uusi_id, 0, j.aktiivinen, j.aloituspaikat, j.kuvaus, j.nimi,
               kopioinnin_uusi_oid(), j.siirretaan_sijoitteluun,
               j.tasapistesaanto, j.ei_varasijatayttoa, j.poissa_oleva_taytto, j.varasijat,
               j.varasijoja_kaytetaan_alkaen, j.varasijoja_taytetaan_asti,
               j.kaytetaan_valintalaskentaa,
               j.kaikki_ehdon_tayttavat_hyvaksytaan, j.valisijoittelu,
               j.automaattinen_sijoitteluun_siirto,
               j.poistetaanko_hylatyt, j.tyyppi, j.merkitse_myoh_auto,
               -- varasijan täyttöjonoa eikä ei_lasketa_paivamaaran_jalkeen-arvoa kopioida,
               -- kuten ei aiemmassakaan toteutuksessa (ValintatapajonoUtil)
               null, null,
               vvm.uusi_id, %s, em.uusi_id
          from kopiointi_map m
          join valintatapajono j on j.id = m.vanha_id
          join kopiointi_map vvm
                 on vvm.tyyppi = 'valinnan_vaihe' and vvm.kohde_id = m.kohde_id
                and vvm.vanha_id = j.valinnan_vaihe_id
          %s
          left join kopiointi_map em
                 on em.tyyppi = 'valintatapajono' and em.kohde_id = m.kohde_id
                and em.vanha_id = j.edellinen_valintatapajono_id
         where m.tyyppi = 'valintatapajono' and %s
        """)
        .formatted(
            juureen ? "mm.uusi_id" : "j.id",
            juureen ? masterLiitos("valintatapajono", "j.master_valintatapajono_id") : tasoLiitos(),
            rajaus(juureen));
  }

  private String kriteeriInsert(boolean juureen) {
    return ("""
        insert into jarjestyskriteeri
            (id, version, aktiivinen, metatiedot, oid, valintatapajono_id, laskentakaava_id,
             master_jarjestyskriteeri_id, edellinen_jarjestyskriteeri_id)
        select m.uusi_id, 0, jk.aktiivinen, jk.metatiedot, kopioinnin_uusi_oid(),
               jm.uusi_id, %s, %s, em.uusi_id
          from kopiointi_map m
          join jarjestyskriteeri jk on jk.id = m.vanha_id
          join kopiointi_map jm
                 on jm.tyyppi = 'valintatapajono' and jm.kohde_id = m.kohde_id
                and jm.vanha_id = jk.valintatapajono_id
          %s
          %s
          left join kopiointi_map em
                 on em.tyyppi = 'jarjestyskriteeri' and em.kohde_id = m.kohde_id
                and em.vanha_id = jk.edellinen_jarjestyskriteeri_id
         where m.tyyppi = 'jarjestyskriteeri' and %s
        """)
        .formatted(
            // Kaavaviite osoitetaan kopioon aina kun kaavasta on tehty kopio: aiempi toteutus
            // löysi kopion haeLaskentakaavaTaiSenKopioVanhemmilta-haun
            // kopioLaskentakaavasta-vertailulla myös perityssä polussa.
            "coalesce(lkm.uusi_id, jk.laskentakaava_id)",
            juureen ? "mm.uusi_id" : "jk.id",
            kaavaLiitos("jk.laskentakaava_id"),
            juureen
                ? masterLiitos("jarjestyskriteeri", "jk.master_jarjestyskriteeri_id")
                : tasoLiitos(),
            rajaus(juureen));
  }

  private String valintakoeInsert(boolean juureen) {
    return ("""
        insert into valintakoe
            (id, version, aktiivinen, kuvaus, nimi, oid, tunniste, laskentakaava_id,
             master_valintakoe_id, valinnan_vaihe_id, lahetetaanko_koekutsut, kutsutaanko_kaikki,
             kutsuttavien_maara, kutsun_kohde, kutsun_kohde_avain)
        select m.uusi_id, 0, vk.aktiivinen, vk.kuvaus, vk.nimi, kopioinnin_uusi_oid(),
               vk.tunniste, %s,
               %s, vvm.uusi_id, vk.lahetetaanko_koekutsut, vk.kutsutaanko_kaikki,
               vk.kutsuttavien_maara, vk.kutsun_kohde, vk.kutsun_kohde_avain
          from kopiointi_map m
          join valintakoe vk on vk.id = m.vanha_id
          join kopiointi_map vvm
                 on vvm.tyyppi = 'valinnan_vaihe' and vvm.kohde_id = m.kohde_id
                and vvm.vanha_id = vk.valinnan_vaihe_id
          %s
          %s
         where m.tyyppi = 'valintakoe' and %s
        """)
        .formatted(
            "coalesce(lkm.uusi_id, vk.laskentakaava_id)",
            juureen ? "mm.uusi_id" : "vk.id",
            kaavaLiitos("vk.laskentakaava_id"),
            juureen ? masterLiitos("valintakoe", "vk.master_valintakoe_id") : tasoLiitos(),
            rajaus(juureen));
  }

  private String hakijaryhmaJonoInsert(boolean juureen) {
    // Huom: tarkkakiintio otetaan lähteen kaytakaikki-sarakkeesta, koska aiempi toteutus
    // (HakijaryhmaValintatapajonoUtil.teeKopioMasterista) tekee niin. Käyttäytyminen on
    // säilytetty ennallaan tarkoituksella.
    //
    // Perityssä kopiossa hakijaryhmäviite jää osoittamaan vanhemman hakijaryhmään, kuten
    // aiemmassakin toteutuksessa; juureen kopioitaessa se osoitetaan kopioon.
    return ("""
        insert into hakijaryhma_jono
            (id, version, aktiivinen, oid, hakijaryhma_id, valintatapajono_id, hakukohde_viite_id,
             tarkkakiintio, kaytakaikki, kiintio, kaytetaan_ryhmaan_kuuluvia,
             hakijaryhmatyyppikoodi_id, master_hakijaryhma_jono_id, edellinen_hakijaryhma_jono_id)
        select m.uusi_id, 0, hj.aktiivinen, kopioinnin_uusi_oid(),
               %s, jm.uusi_id, null,
               hj.kaytakaikki, hj.kaytakaikki, hj.kiintio, hj.kaytetaan_ryhmaan_kuuluvia,
               hj.hakijaryhmatyyppikoodi_id, %s, em.uusi_id
          from kopiointi_map m
          join hakijaryhma_jono hj on hj.id = m.vanha_id
          join kopiointi_map jm
                 on jm.tyyppi = 'valintatapajono' and jm.kohde_id = m.kohde_id
                and jm.vanha_id = hj.valintatapajono_id
          %s
          %s
          left join kopiointi_map em
                 on em.tyyppi = 'hakijaryhma_jono' and em.kohde_id = m.kohde_id
                and em.vanha_id = hj.edellinen_hakijaryhma_jono_id
         where m.tyyppi = 'hakijaryhma_jono' and %s
        """)
        .formatted(
            juureen ? "hrm.uusi_id" : "hj.hakijaryhma_id",
            juureen ? "mm.uusi_id" : "hj.id",
            juureen
                ? """
                  join kopiointi_map hrm
                         on hrm.tyyppi = 'hakijaryhma' and hrm.kohde_id = m.kohde_id
                        and hrm.vanha_id = hj.hakijaryhma_id
                  """
                : "",
            juureen
                ? masterLiitos("hakijaryhma_jono", "hj.master_hakijaryhma_jono_id")
                : tasoLiitos(),
            rajaus(juureen));
  }

  private String masterLiitos(String tyyppi, String masterSarake) {
    return ("""
        left join kopiointi_map mm
                 on mm.tyyppi = '%s' and mm.kohde_id = m.kohde_id
                and mm.vanha_id = %s
        """)
        .formatted(tyyppi, masterSarake);
  }

  private String kaavaLiitos(String kaavaSarake) {
    return ("""
        left join kopiointi_map lkm
                 on lkm.tyyppi = 'laskentakaava' and lkm.kohde_id = 0
                and lkm.vanha_id = %s
        """)
        .formatted(kaavaSarake);
  }

  private String tasoLiitos() {
    return "join kopiointi_peritty p on p.kohde_id = m.kohde_id";
  }

  private String rajaus(boolean juureen) {
    return juureen ? "m.kohde_id = 0" : "p.syvyys = ?";
  }

  private String haeKopioidunJuurenOid(Connection c) throws SQLException {
    try (PreparedStatement ps =
            c.prepareStatement(
                """
                select vr.oid from valintaryhma vr
                  join kopiointi_map m on m.uusi_id = vr.id
                 where m.tyyppi = 'valintaryhma' and m.kohde_id = 0 and m.syvyys = 0
                """);
        ResultSet rs = ps.executeQuery()) {
      if (!rs.next()) {
        throw new IllegalStateException("Kopioidun valintaryhmän juurta ei löytynyt");
      }
      return rs.getString(1);
    }
  }

  private int paivita(Connection c, String sql, Object... parametrit) throws SQLException {
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      for (int i = 0; i < parametrit.length; i++) {
        Object parametri = parametrit[i];
        if (parametri == null) {
          ps.setNull(i + 1, Types.BIGINT);
        } else if (parametri instanceof Long pitka) {
          ps.setLong(i + 1, pitka);
        } else if (parametri instanceof Integer kokonais) {
          ps.setInt(i + 1, kokonais);
        } else {
          ps.setString(i + 1, parametri.toString());
        }
      }
      return ps.executeUpdate();
    }
  }
}
