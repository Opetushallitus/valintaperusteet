package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dto.ArvokonvertteriparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.ArvovalikonvertteriparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumentinLapsiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumenttiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LokalisoituTekstiDTO;
import fi.vm.sade.service.valintaperusteet.dto.SyoteparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.TekstiRyhmaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.ArvokonvertteriparametriId;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.ArvovalikonvertteriparametriId;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.FunktioargumenttiId;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.FunktiokutsuId;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViiteId;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti;
import fi.vm.sade.service.valintaperusteet.model.LokalisoituTekstiId;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import fi.vm.sade.service.valintaperusteet.model.SyoteparametriId;
import fi.vm.sade.service.valintaperusteet.model.Syotettavanarvontyyppi;
import fi.vm.sade.service.valintaperusteet.model.SyotettavanarvontyyppiId;
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma;
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhmaId;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViiteId;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.ExceptionUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class LaskentakaavaDAOImpl implements LaskentakaavaDAO {

  private static String FUNKTIOKUTSU_TREE_CTE = "" +
          "tree(id) as (\n" +
          "    select id\n" +
          "    from funktiokutsu\n" +
          "    where id = ?\n" +
          "    union all\n" +
          "    select f.id\n" +
          "    from tree\n" +
          "    join funktioargumentti as fa\n" +
          "        on fa.funktiokutsuparent_id = tree.id\n" +
          "    join funktiokutsu as f\n" +
          "        on f.id = fa.funktiokutsuchild_id\n" +
          ")";

  @Autowired
  private DataSource dataSource;

  private static class FunktioargumenttiBuilder {
    private final FunktioargumenttiId id;
    private final FunktiokutsuId funktiokutsuId;
    private final LaskentakaavaId laskentakaavaId;
    private final int indeksi;

    public FunktioargumenttiBuilder(FunktioargumenttiId id,
                                    FunktiokutsuId funktiokutsuId,
                                    LaskentakaavaId laskentakaavaId,
                                    int indeksi) {
      this.id = id;
      this.funktiokutsuId = funktiokutsuId;
      this.laskentakaavaId = laskentakaavaId;
      this.indeksi = indeksi;
    }

    public Funktioargumentti build(Map<FunktiokutsuId, FunktiokutsuBuilder> funktiokutsuBuilders,
                                   Map<LaskentakaavaId, Laskentakaava> laskentakaavat,
                                   Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> arvokonvertteriparametrit,
                                   Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> arvovalikonvertteriparametrit,
                                   Map<FunktiokutsuId, Set<Syoteparametri>> syoteparametrit,
                                   Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> funktioargumenttiBuilders,
                                   Map<FunktiokutsuId, List<ValintaperusteViite>> valintaperusteViitteet) {
      return new Funktioargumentti(
              id,
              0,
              funktiokutsuId == null ? null : funktiokutsuBuilders.get(funktiokutsuId).build(
                      funktiokutsuBuilders,
                      laskentakaavat,
                      arvokonvertteriparametrit,
                      arvovalikonvertteriparametrit,
                      syoteparametrit,
                      funktioargumenttiBuilders,
                      valintaperusteViitteet
              ),
              laskentakaavaId == null ? null : laskentakaavat.get(laskentakaavaId),
              indeksi
      );
    }
  }

  private static class FunktiokutsuBuilder {
    private final FunktiokutsuId id;
    private final Funktionimi funktionimi;
    private final String tulosTunniste;
    private final String getTulosTekstiFi;
    private final String getTulosTekstiSv;
    private final String getTulosTekstiEn;
    private final Boolean tallennaTulos;
    private final boolean omaOpintopolku;

    public FunktiokutsuBuilder(FunktiokutsuId id,
                               Funktionimi funktionimi,
                               String tulosTunniste,
                               String getTulosTekstiFi,
                               String getTulosTekstiSv,
                               String getTulosTekstiEn,
                               Boolean tallennaTulos,
                               boolean omaOpintopolku) {
      this.id = id;
      this.funktionimi = funktionimi;
      this.tulosTunniste = tulosTunniste;
      this.getTulosTekstiFi = getTulosTekstiFi;
      this.getTulosTekstiSv = getTulosTekstiSv;
      this.getTulosTekstiEn = getTulosTekstiEn;
      this.tallennaTulos = tallennaTulos;
      this.omaOpintopolku = omaOpintopolku;
    }

    public Funktiokutsu build(Map<FunktiokutsuId, FunktiokutsuBuilder> funktiokutsuBuilders,
                              Map<LaskentakaavaId, Laskentakaava> laskentakaavat,
                              Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> arvokonvertteriparametrit,
                              Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> arvovalikonvertteriparametrit,
                              Map<FunktiokutsuId, Set<Syoteparametri>> syoteparametrit,
                              Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> funktioargumenttiBuilders,
                              Map<FunktiokutsuId, List<ValintaperusteViite>> valintaperusteViitteet) {
      return new Funktiokutsu(
              id,
              0,
              funktionimi,
              tulosTunniste,
              getTulosTekstiFi,
              getTulosTekstiSv,
              getTulosTekstiEn,
              tallennaTulos == null ? false : tallennaTulos,
              omaOpintopolku,
              arvokonvertteriparametrit.getOrDefault(id, new HashSet<>()),
              arvovalikonvertteriparametrit.getOrDefault(id, new ArrayList<>()).stream()
                      .sorted()
                      .collect(Collectors.toList()),
              syoteparametrit.getOrDefault(id, new HashSet<>()),
              funktioargumenttiBuilders.getOrDefault(id, new ArrayList<>()).stream()
                      .map(faBuilder -> faBuilder.build(
                              funktiokutsuBuilders,
                              laskentakaavat,
                              arvokonvertteriparametrit,
                              arvovalikonvertteriparametrit,
                              syoteparametrit,
                              funktioargumenttiBuilders,
                              valintaperusteViitteet
                      ))
                      .sorted()
                      .collect(Collectors.toList()),
              valintaperusteViitteet.getOrDefault(id, new ArrayList<>()).stream()
                      .sorted()
                      .collect(Collectors.toList())
      );
    }
  }

  private List<Pair<TekstiRyhmaId, LokalisoituTeksti>> insertLokalisoidutTekstit(List<Pair<TekstiRyhmaId, LokalisoituTekstiDTO>> dtos) {
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into lokalisoitu_teksti (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    kieli,\n" +
                            "    teksti,\n" +
                            "    tekstiryhma_id\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning tekstiryhma_id,\n" +
                            "            id,\n" +
                            "            version,\n" +
                            "            kieli,\n" +
                            "            teksti",
                    new String[] {
                            "tekstiryhma_id",
                            "id",
                            "version",
                            "kieli",
                            "teksti"
                    }
            ),
            (PreparedStatementCallback<List<Pair<TekstiRyhmaId, LokalisoituTeksti>>>) ps -> {
              dtos.forEach(p -> {
                TekstiRyhmaId tekstiryhmaId = p.getLeft();
                LokalisoituTekstiDTO dto = p.getRight();
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getKieli().name());
                  ps.setString(3, dto.getTeksti());
                  ps.setLong(4, tekstiryhmaId.id);
                  ps.addBatch();
                } catch (SQLException ex) {
                  ExceptionUtil.rethrow(ex);
                }
              });
              ps.executeBatch();
              return new RowMapperResultSetExtractor<>((rs, rowNum) -> Pair.of(
                      new TekstiRyhmaId(rs.getLong("tekstiryhma_id")),
                      new LokalisoituTeksti(
                              new LokalisoituTekstiId(rs.getLong("id")),
                              rs.getLong("version"),
                              rs.getString("teksti"),
                              Kieli.valueOf(rs.getString("kieli"))
                      )))
                      .extractData(ps.getGeneratedKeys());
            });
  }

  private <T> List<Pair<T, TekstiRyhma>> insertTekstiryhmat(List<Pair<T, TekstiRyhmaDTO>> dtos) {
    List<Pair<T, Pair<TekstiRyhmaId, TekstiRyhmaDTO>>> ids = new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into tekstiryhma (\n" +
                            "    id,\n" +
                            "    version\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?\n" +
                            ")\n" +
                            "returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<List<Pair<T, Pair<TekstiRyhmaId, TekstiRyhmaDTO>>>>) ps -> {
              dtos.forEach(akp -> {
                try {
                  ps.setLong(1, 0);
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<TekstiRyhmaId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new TekstiRyhmaId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return dtos.stream().map(p -> Pair.of(p.getLeft(), Pair.of(idI.next(), p.getRight()))).collect(Collectors.toList());
            }
    );
    Map<TekstiRyhmaId, Set<LokalisoituTeksti>> lts = insertLokalisoidutTekstit(
            ids.stream().flatMap(p -> p.getRight().getRight().getTekstit().stream().map(ltDto -> Pair.of(p.getRight().getLeft(), ltDto))).collect(Collectors.toList())
    ).stream().collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toSet())));
    return ids.stream().map(p -> Pair.of(p.getLeft(), new TekstiRyhma(p.getRight().getLeft(), 0, lts.getOrDefault(p.getRight().getLeft(), new HashSet<>())))).collect(Collectors.toList());
  }

  private Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> insertArvokonvertteriparametrit(List<Pair<FunktiokutsuId, ArvokonvertteriparametriDTO>> dtos) {
    List<Pair<Pair<FunktiokutsuId, ArvokonvertteriparametriDTO>, TekstiRyhma>> x = insertTekstiryhmat(
            dtos.stream().map(p -> Pair.of(p, p.getRight().getKuvaukset())).collect(Collectors.toList())
    );
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into arvokonvertteriparametri (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    hylkaysperuste,\n" +
                            "    paluuarvo,\n" +
                            "    arvo,\n" +
                            "    funktiokutsu_id,\n" +
                            "    tekstiryhma_id\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<Map<FunktiokutsuId, Set<Arvokonvertteriparametri>>>) ps -> {
              x.forEach(p -> {
                FunktiokutsuId funktiokutsuId = p.getLeft().getLeft();
                TekstiRyhmaId tekstiRyhmaId = p.getRight().getId();
                ArvokonvertteriparametriDTO dto = p.getLeft().getRight();
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getHylkaysperuste() == null ? "false" : dto.getHylkaysperuste());
                  ps.setString(3, dto.getPaluuarvo());
                  ps.setString(4, dto.getArvo());
                  ps.setLong(5, funktiokutsuId.id);
                  ps.setLong(6, tekstiRyhmaId.id);
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<ArvokonvertteriparametriId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new ArvokonvertteriparametriId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return x.stream().map(p -> {
                ArvokonvertteriparametriDTO dto = p.getLeft().getRight();
                return Pair.of(p.getLeft().getLeft(), new Arvokonvertteriparametri(
                        idI.next(),
                        0,
                        dto.getPaluuarvo(),
                        dto.getArvo(),
                        dto.getHylkaysperuste(),
                        p.getRight()
                ));
              }).collect(Collectors.groupingBy(
                      Pair::getLeft,
                      Collectors.mapping(Pair::getRight, Collectors.toSet())
              ));
            }
    );
  }

  private Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> insertArvovalikonvertteriparametrit(List<Pair<FunktiokutsuId, ArvovalikonvertteriparametriDTO>> dtos) {
    List<Pair<Pair<FunktiokutsuId, ArvovalikonvertteriparametriDTO>, TekstiRyhma>> x = insertTekstiryhmat(
            dtos.stream().map(p -> Pair.of(p, p.getRight().getKuvaukset())).collect(Collectors.toList())
    );
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into arvovalikonvertteriparametri (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    paluuarvo,\n" +
                            "    maxvalue,\n" +
                            "    minvalue,\n" +
                            "    palauta_haettu_arvo,\n" +
                            "    funktiokutsu_id,\n" +
                            "    hylkaysperuste,\n" +
                            "    tekstiryhma_id\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>>>) ps -> {
              x.forEach(p -> {
                ArvovalikonvertteriparametriDTO dto = p.getLeft().getRight();
                FunktiokutsuId funktiokutsuId = p.getLeft().getLeft();
                TekstiRyhmaId tekstiryhmaId = p.getRight().getId();
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getPaluuarvo());
                  ps.setString(3, dto.getMaxValue());
                  ps.setString(4, dto.getMinValue());
                  ps.setString(5, dto.getPalautaHaettuArvo() == null ? "false" : dto.getPalautaHaettuArvo());
                  ps.setLong(6, funktiokutsuId.id);
                  ps.setString(7, dto.getHylkaysperuste());
                  ps.setLong(8, tekstiryhmaId.id);
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<ArvovalikonvertteriparametriId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new ArvovalikonvertteriparametriId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return x.stream().map(p -> {
                ArvovalikonvertteriparametriDTO dto = p.getLeft().getRight();
                return Pair.of(p.getLeft().getLeft(), new Arvovalikonvertteriparametri(
                        idI.next(),
                        0,
                        dto.getPaluuarvo(),
                        dto.getMinValue(),
                        dto.getMaxValue(),
                        dto.getPalautaHaettuArvo() == null ? "false" : dto.getPalautaHaettuArvo(),
                        dto.getHylkaysperuste(),
                        p.getRight()
                ));
              }).collect(Collectors.groupingBy(
                      Pair::getLeft,
                      Collectors.mapping(Pair::getRight, Collectors.toList())
              ));
            }
    );
  }

  private Map<FunktiokutsuId, Set<Syoteparametri>> insertSyoteparametrit(List<Pair<FunktiokutsuId, SyoteparametriDTO>> dtos) {
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into syoteparametri (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    arvo,\n" +
                            "    avain,\n" +
                            "    funktiokutsu_id\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning funktiokutsu_id,\n" +
                            "            id,\n" +
                            "            version,\n" +
                            "            arvo,\n" +
                            "            avain",
                    new String[] {
                            "funktiokutsu_id",
                            "id",
                            "version",
                            "arvo",
                            "avain"
                    }
            ),
            (PreparedStatementCallback<Map<FunktiokutsuId, Set<Syoteparametri>>>) ps -> {
              dtos.forEach(p -> {
                SyoteparametriDTO dto = p.getRight();
                FunktiokutsuId funktiokutsuId = p.getLeft();
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getArvo() == null ? "" : dto.getArvo());
                  ps.setString(3, dto.getAvain());
                  ps.setLong(4, funktiokutsuId.id);
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              return new RowMapperResultSetExtractor<>((rs, rowNum) -> Pair.of(
                      new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                      new Syoteparametri(
                              new SyoteparametriId(rs.getLong("id")),
                              rs.getLong("version"),
                              rs.getString("avain"),
                              rs.getString("arvo")
                      )
              )).extractData(ps.getGeneratedKeys()).stream()
                      .collect(Collectors.groupingBy(
                              Pair::getLeft,
                              Collectors.mapping(Pair::getRight, Collectors.toSet())
                      ));
            }
    );
  }

  private Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> insertFunktioargumentit(List<Triple<FunktiokutsuId, FunktiokutsuId, FunktioargumenttiDTO>> dtos) {
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into funktioargumentti (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    indeksi,\n" +
                            "    funktiokutsuchild_id,\n" +
                            "    laskentakaavachild_id,\n" +
                            "    funktiokutsuparent_id\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<Map<FunktiokutsuId, List<FunktioargumenttiBuilder>>>) ps -> {
              dtos.forEach(p -> {
                FunktiokutsuId parentId = p.getLeft();
                FunktiokutsuId childId = p.getMiddle();
                FunktioargumenttiDTO dto = p.getRight();
                Long laskentakaavaId = dto.getLapsi().getId();
                try {
                  ps.setLong(1, 0);
                  ps.setInt(2, dto.getIndeksi());
                  if (childId == null) {
                    ps.setNull(3, Types.BIGINT);
                  } else {
                    ps.setLong(3, childId.id);
                  }
                  if (laskentakaavaId == null) {
                    ps.setNull(4, Types.BIGINT);
                  } else {
                    ps.setLong(4, laskentakaavaId);
                  }
                  ps.setLong(5, parentId.id);
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<FunktioargumenttiId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new FunktioargumenttiId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return dtos.stream()
                      .map(p -> {
                        FunktioargumenttiDTO dto = p.getRight();
                        return Pair.of(
                                p.getLeft(),
                                new FunktioargumenttiBuilder(
                                        idI.next(),
                                        p.getMiddle(),
                                        dto.getLapsi().getId() == null ? null : new LaskentakaavaId(dto.getLapsi().getId()),
                                        dto.getIndeksi()
                                )
                        );
                      }).collect(Collectors.groupingBy(
                              Pair::getLeft,
                              Collectors.mapping(Pair::getRight, Collectors.toList())
                      ));
            }
    );
  }

  private <T> List<Pair<T, Syotettavanarvontyyppi>> insertSyotettavanarvontyypit(List<Pair<T, KoodiDTO>> dtos) {
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into syotettavanarvonkoodi (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    arvo,\n" +
                            "    nimi_en,\n" +
                            "    nimi_fi,\n" +
                            "    nimi_sv,\n" +
                            "    uri\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ")\n" +
                            "on conflict (uri) do update set arvo = excluded.arvo,\n" +
                            "                                nimi_en = excluded.nimi_en,\n" +
                            "                                nimi_fi = excluded.nimi_fi,\n" +
                            "                                nimi_sv = excluded.nimi_sv\n" +
                            "returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<List<Pair<T, Syotettavanarvontyyppi>>>) ps -> {
              dtos.forEach(p -> {
                KoodiDTO dto = p.getRight();
                if (dto != null) {
                  try {
                    ps.setLong(1, 0);
                    ps.setString(2, dto.getArvo());
                    ps.setString(3, dto.getNimiEn());
                    ps.setString(4, dto.getNimiFi());
                    ps.setString(5, dto.getNimiSv());
                    ps.setString(6, dto.getUri());
                    ps.addBatch();
                  } catch (SQLException e) {
                    ExceptionUtil.rethrow(e);
                  }
                }
              });
              ps.executeBatch();
              Iterator<SyotettavanarvontyyppiId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new SyotettavanarvontyyppiId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return dtos.stream().map(p -> {
                KoodiDTO dto = p.getRight();
                return Pair.of(p.getLeft(), dto == null ? null : new Syotettavanarvontyyppi(
                        idI.next(),
                        0,
                        dto.getUri(),
                        dto.getNimiFi(),
                        dto.getNimiSv(),
                        dto.getNimiEn(),
                        dto.getArvo()
                ));
              }).collect(Collectors.toList());
            }
    );
  }

  private Map<FunktiokutsuId, List<ValintaperusteViite>> insertValintaperusteViitteet(List<Pair<FunktiokutsuId, ValintaperusteViiteDTO>> dtos) {
    List<Pair<Pair<Pair<FunktiokutsuId, ValintaperusteViiteDTO>, Syotettavanarvontyyppi>, TekstiRyhma>> x = insertTekstiryhmat(
            insertSyotettavanarvontyypit(dtos.stream().map(p -> Pair.of(p, p.getRight().getSyotettavanarvontyyppi())).collect(Collectors.toList())).stream()
                    .map(p -> Pair.of(p, p.getLeft().getRight().getKuvaukset())).collect(Collectors.toList())
    );
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into valintaperuste_viite (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    kuvaus,\n" +
                            "    lahde,\n" +
                            "    on_pakollinen,\n" +
                            "    tunniste,\n" +
                            "    funktiokutsu_id,\n" +
                            "    epasuora_viittaus,\n" +
                            "    indeksi,\n" +
                            "    tekstiryhma_id,\n" +
                            "    vaatii_osallistumisen,\n" +
                            "    syotettavissa_kaikille,\n" +
                            "    syotettavanarvontyyppi_id,\n" +
                            "    tilastoidaan\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ") returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<Map<FunktiokutsuId, List<ValintaperusteViite>>>) ps -> {
              x.forEach(p -> {
                ValintaperusteViiteDTO dto = p.getLeft().getLeft().getRight();
                FunktiokutsuId funktiokutsuId = p.getLeft().getLeft().getLeft();
                TekstiRyhmaId tekstiryhmaId = p.getRight().getId();
                Syotettavanarvontyyppi syotettavanarvontyyppi = p.getLeft().getRight();
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getKuvaus());
                  ps.setString(3, dto.getLahde().name());
                  ps.setBoolean(4, dto.getOnPakollinen() == null ? false : dto.getOnPakollinen());
                  ps.setString(5, dto.getTunniste());
                  ps.setLong(6, funktiokutsuId.id);
                  ps.setBoolean(7, dto.getEpasuoraViittaus() == null ? false : dto.getEpasuoraViittaus());
                  ps.setInt(8, dto.getIndeksi());
                  ps.setLong(9, tekstiryhmaId.id);
                  ps.setBoolean(10, dto.getVaatiiOsallistumisen() == null ? true : dto.getVaatiiOsallistumisen());
                  ps.setBoolean(11, dto.getSyotettavissaKaikille() == null ? true : dto.getSyotettavissaKaikille());
                  if (syotettavanarvontyyppi == null) {
                    ps.setNull(12, Types.BIGINT);
                  } else {
                    ps.setLong(12, syotettavanarvontyyppi.getId().id);
                  }
                  ps.setBoolean(13, dto.isTilastoidaan());
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<ValintaperusteViiteId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new ValintaperusteViiteId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return x.stream().map(p -> {
                ValintaperusteViiteDTO dto = p.getLeft().getLeft().getRight();
                return Pair.of(p.getLeft().getLeft().getLeft(), new ValintaperusteViite(
                        idI.next(),
                        0,
                        dto.getTunniste(),
                        dto.getKuvaus(),
                        dto.getLahde(),
                        dto.getOnPakollinen() == null ? false : dto.getOnPakollinen(),
                        dto.getEpasuoraViittaus() == null ? false : dto.getEpasuoraViittaus(),
                        dto.getIndeksi(),
                        p.getRight(),
                        dto.getVaatiiOsallistumisen() == null ? true : dto.getVaatiiOsallistumisen(),
                        dto.getSyotettavissaKaikille() == null ? true : dto.getSyotettavissaKaikille(),
                        p.getLeft().getRight(),
                        dto.isTilastoidaan()
                ));
              }).collect(Collectors.groupingBy(
                      Pair::getLeft,
                      Collectors.mapping(Pair::getRight, Collectors.toList())
              ));
            }
    );
  }

  private List<Pair<FunktiokutsuBuilder, FunktiokutsuDTO>> insertFunktiokutsut(List<FunktiokutsuDTO> dtos) {
    return new JdbcTemplate(this.dataSource).execute(
            con -> con.prepareStatement(
                    "" +
                            "insert into funktiokutsu (\n" +
                            "    id,\n" +
                            "    version,\n" +
                            "    funktionimi,\n" +
                            "    tallenna_tulos,\n" +
                            "    tulos_tunniste,\n" +
                            "    tulos_teksti_fi,\n" +
                            "    tulos_teksti_sv,\n" +
                            "    tulos_teksti_en,\n" +
                            "    oma_opintopolku\n" +
                            ") values (\n" +
                            "    nextval('hibernate_sequence'),\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?,\n" +
                            "    ?\n" +
                            ")\n" +
                            "returning id",
                    new String[] {"id"}
            ),
            (PreparedStatementCallback<List<Pair<FunktiokutsuBuilder, FunktiokutsuDTO>>>) ps -> {
              dtos.forEach(dto -> {
                try {
                  ps.setLong(1, 0);
                  ps.setString(2, dto.getFunktionimi().name());
                  ps.setBoolean(3, dto.getTallennaTulos() == null ? false : dto.getTallennaTulos());
                  ps.setString(4, dto.getTulosTunniste());
                  ps.setString(5, dto.getTulosTekstiFi());
                  ps.setString(6, dto.getTulosTekstiSv());
                  ps.setString(7, dto.getTulosTekstiEn());
                  ps.setBoolean(8, dto.getOmaopintopolku());
                  ps.addBatch();
                } catch (SQLException e) {
                  ExceptionUtil.rethrow(e);
                }
              });
              ps.executeBatch();
              Iterator<FunktiokutsuId> idI = new RowMapperResultSetExtractor<>((rs, rowNum) -> new FunktiokutsuId(rs.getLong("id")))
                      .extractData(ps.getGeneratedKeys()).iterator();
              return dtos.stream().map(p -> Pair.of(
                      new FunktiokutsuBuilder(
                              idI.next(),
                              p.getFunktionimi(),
                              p.getTulosTunniste(),
                              p.getTulosTekstiFi(),
                              p.getTulosTekstiSv(),
                              p.getTulosTekstiEn(),
                              p.getTallennaTulos(),
                              p.getOmaopintopolku()
                      ),
                      p
              )).collect(Collectors.toList());
            }
    );
  }

  private <T> void argumentitEsijarjestyksessa(FunktiokutsuDTO dto, T fromParent, BiFunction<T, FunktioargumenttiDTO, T> f) {
    LinkedList<Pair<T, FunktiokutsuDTO>> stack = new LinkedList<>();
    stack.push(Pair.of(fromParent, dto));
    while (!stack.isEmpty()) {
      Pair<T, FunktiokutsuDTO> p = stack.pop();
      for (FunktioargumenttiDTO fa : p.getRight().getFunktioargumentit()) {
        T toChildren = f.apply(p.getLeft(), fa);
        if (fa.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
          stack.push(Pair.of(toChildren, new FunktiokutsuDTO(fa.getLapsi())));
        }
      }
    }
  }

  private Funktiokutsu insert(FunktiokutsuDTO dto, String nimi) {
    // Asetetaan juurikutsun nimeksi laskentakaavan nimi
    dto.getSyoteparametrit().forEach(sp -> {
      if (sp.getAvain().equals("nimi")) {
        sp.setArvo(nimi);
      }
    });

    LinkedList<FunktiokutsuDTO> funktiokutsuDtos = new LinkedList<>();
    Map<LaskentakaavaId, Laskentakaava> laskentakaavat = new HashMap<>();
    funktiokutsuDtos.add(dto);
    argumentitEsijarjestyksessa(dto, null, (n, fa) -> {
      if (fa.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
        funktiokutsuDtos.add(new FunktiokutsuDTO(fa.getLapsi()));
      } else {
        LaskentakaavaId laskentakaavaId = new LaskentakaavaId(fa.getLapsi().getId());
        if (!laskentakaavat.containsKey(laskentakaavaId)) {
          laskentakaavat.put(laskentakaavaId, this.read(laskentakaavaId));
        }
      }
      return null;
    });

    List<Pair<FunktiokutsuBuilder, FunktiokutsuDTO>> funktiokutsuBuilders = insertFunktiokutsut(funktiokutsuDtos);

    Iterator<FunktiokutsuBuilder> funktiokutsuBuilderI = funktiokutsuBuilders.stream().map(Pair::getLeft).iterator();
    FunktiokutsuBuilder rootBuilder = funktiokutsuBuilderI.next();

    List<Triple<FunktiokutsuId, FunktiokutsuId, FunktioargumenttiDTO>> funktioargumenttiDtos = new LinkedList<>();
    argumentitEsijarjestyksessa(dto, rootBuilder.id, (parentId, fa) -> {
      if (fa.getLapsi().getLapsityyppi().equals(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI)) {
        FunktiokutsuId id = funktiokutsuBuilderI.next().id;
        funktioargumenttiDtos.add(Triple.of(parentId, id, fa));
        return id;
      } else {
        funktioargumenttiDtos.add(Triple.of(parentId, null, fa));
        return null;
      }
    });

    Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> funktioargumenttiBuilders = insertFunktioargumentit(funktioargumenttiDtos);

    Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> arvokonvertteriparametrit = insertArvokonvertteriparametrit(
            funktiokutsuBuilders.stream()
                    .flatMap(p -> p.getRight().getArvokonvertteriparametrit().stream()
                            .map(akp -> Pair.of(p.getLeft().id, akp)))
                    .collect(Collectors.toList())
    );

    Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> arvovalikonvertteriparametrit = insertArvovalikonvertteriparametrit(
            funktiokutsuBuilders.stream()
                    .flatMap(p -> p.getRight().getArvovalikonvertteriparametrit().stream()
                            .map(avkp -> Pair.of(p.getLeft().id, avkp)))
                    .collect(Collectors.toList())
    );

    Map<FunktiokutsuId, Set<Syoteparametri>> syoteparametrit = insertSyoteparametrit(
            funktiokutsuBuilders.stream()
                    .flatMap(p -> p.getRight().getSyoteparametrit().stream()
                            .map(sp -> Pair.of(p.getLeft().id, sp)))
                    .collect(Collectors.toList())
    );

    Map<FunktiokutsuId, List<ValintaperusteViite>> valintaperusteViitteet = insertValintaperusteViitteet(
            funktiokutsuBuilders.stream()
                    .flatMap(p -> p.getRight().getValintaperusteviitteet().stream()
                            .map(vpv -> Pair.of(p.getLeft().id, vpv)))
                    .collect(Collectors.toList())
    );

    return rootBuilder.build(
            funktiokutsuBuilders.stream().collect(Collectors.toMap(p -> p.getLeft().id, Pair::getLeft)),
            laskentakaavat,
            arvokonvertteriparametrit,
            arvovalikonvertteriparametrit,
            syoteparametrit,
            funktioargumenttiBuilders,
            valintaperusteViitteet
    );
  }

  private Map<FunktiokutsuId, FunktiokutsuBuilder> readFunktiokutsut(FunktiokutsuId rootId) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select funktiokutsu.id,\n" +
                    "       version,\n" +
                    "       funktionimi,\n" +
                    "       tallenna_tulos,\n" +
                    "       tulos_tunniste,\n" +
                    "       tulos_teksti_fi,\n" +
                    "       tulos_teksti_sv,\n" +
                    "       tulos_teksti_en,\n" +
                    "       oma_opintopolku\n" +
                    "from tree\n" +
                    "join funktiokutsu\n" +
                    "    on funktiokutsu.id = tree.id",
            (rs, rowNum) -> {
              FunktiokutsuId id = new FunktiokutsuId(rs.getLong("id"));
              return Pair.of(
                      id,
                      new FunktiokutsuBuilder(
                              id,
                              Funktionimi.valueOf(rs.getString("funktionimi")),
                              rs.getString("tulos_tunniste"),
                              rs.getString("tulos_teksti_fi"),
                              rs.getString("tulos_teksti_sv"),
                              rs.getString("tulos_teksti_en"),
                              rs.getBoolean("tallenna_tulos"),
                              rs.getBoolean("oma_opintopolku")
                      )
              );
            },
            rootId.id
    ).stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
  }

  private Map<TekstiRyhmaId, Set<LokalisoituTeksti>> readLokalisoidutTekstit(FunktiokutsuId funktiokutsuId) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select distinct lt.tekstiryhma_id,\n" +
                    "       lt.id,\n" +
                    "       lt.version,\n" +
                    "       lt.teksti,\n" +
                    "       lt.kieli\n" +
                    "from tree\n" +
                    "left join arvokonvertteriparametri as akp\n" +
                    "    on akp.funktiokutsu_id = tree.id\n" +
                    "left join arvovalikonvertteriparametri as avkp\n" +
                    "    on avkp.funktiokutsu_id = tree.id\n" +
                    "left join valintaperuste_viite as vpv\n" +
                    "    on vpv.funktiokutsu_id = tree.id\n" +
                    "join lokalisoitu_teksti as lt\n" +
                    "    on (lt.tekstiryhma_id = akp.tekstiryhma_id or\n" +
                    "        lt.tekstiryhma_id = avkp.tekstiryhma_id or\n" +
                    "        lt.tekstiryhma_id = vpv.tekstiryhma_id)",
            (rs, rowNum) -> Pair.of(
                    new TekstiRyhmaId(rs.getLong("tekstiryhma_id")),
                    new LokalisoituTeksti(
                            new LokalisoituTekstiId(rs.getLong("id")),
                            rs.getLong("version"),
                            rs.getString("teksti"),
                            Kieli.valueOf(rs.getString("kieli"))
                    )),
            funktiokutsuId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toSet())
    ));
  }

  private Map<TekstiRyhmaId, TekstiRyhma> readTekstiryhmat(FunktiokutsuId funktiokutsuId,
                                                           Map<TekstiRyhmaId, Set<LokalisoituTeksti>> lokalisoidutTekstit) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select distinct tr.id\n" +
                    "from tree\n" +
                    "left join arvokonvertteriparametri as akp\n" +
                    "    on akp.funktiokutsu_id = tree.id\n" +
                    "left join arvovalikonvertteriparametri as avkp\n" +
                    "    on avkp.funktiokutsu_id = tree.id\n" +
                    "left join valintaperuste_viite as vpv\n" +
                    "    on vpv.funktiokutsu_id = tree.id\n" +
                    "join tekstiryhma as tr\n" +
                    "    on (tr.id = akp.tekstiryhma_id or\n" +
                    "        tr.id = avkp.tekstiryhma_id or\n" +
                    "        tr.id = vpv.tekstiryhma_id)",
            (rs, rowNum) -> {
              TekstiRyhmaId id = new TekstiRyhmaId(rs.getLong("id"));
              return new TekstiRyhma(
                      id,
                      0,
                      lokalisoidutTekstit.getOrDefault(id, new HashSet<>())
              );
            },
            funktiokutsuId.id
    ).stream().collect(Collectors.toMap(TekstiRyhma::getId, Function.identity()));
  }

  private Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> readArvokonvertteriparametrit(FunktiokutsuId funktiokutsuId,
                                                                                           Map<TekstiRyhmaId, TekstiRyhma> tekstiryhmat) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select arvokonvertteriparametri.id,\n" +
                    "       version,\n" +
                    "       hylkaysperuste,\n" +
                    "       paluuarvo,\n" +
                    "       arvo,\n" +
                    "       funktiokutsu_id,\n" +
                    "       tekstiryhma_id\n" +
                    "from tree\n" +
                    "join arvokonvertteriparametri\n" +
                    "    on arvokonvertteriparametri.funktiokutsu_id = tree.id",
            (rs, rowNum) -> Pair.of(
                    new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                    new Arvokonvertteriparametri(
                            new ArvokonvertteriparametriId(rs.getLong("id")),
                            rs.getLong("version"),
                            rs.getString("paluuarvo"),
                            rs.getString("arvo"),
                            rs.getString("hylkaysperuste"),
                            tekstiryhmat.get(new TekstiRyhmaId(rs.getLong("tekstiryhma_id")))
                    )
            ),
            funktiokutsuId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toSet())
    ));
  }

  private Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> readArvovalikonvertteriparametrit(FunktiokutsuId funktiokutsuId,
                                                                                                    Map<TekstiRyhmaId, TekstiRyhma> tekstiryhmat) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select arvovalikonvertteriparametri.id,\n" +
                    "       version,\n" +
                    "       paluuarvo,\n" +
                    "       maxvalue,\n" +
                    "       minvalue,\n" +
                    "       palauta_haettu_arvo,\n" +
                    "       funktiokutsu_id,\n" +
                    "       hylkaysperuste,\n" +
                    "       tekstiryhma_id\n" +
                    "from tree\n" +
                    "join arvovalikonvertteriparametri\n" +
                    "    on arvovalikonvertteriparametri.funktiokutsu_id = tree.id",
            (rs, rowNum) -> Pair.of(
                    new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                    new Arvovalikonvertteriparametri(
                            new ArvovalikonvertteriparametriId(rs.getLong("id")),
                            rs.getLong("version"),
                            rs.getString("paluuarvo"),
                            rs.getString("minvalue"),
                            rs.getString("maxvalue"),
                            rs.getString("palauta_haettu_arvo"),
                            rs.getString("hylkaysperuste"),
                            tekstiryhmat.get(new TekstiRyhmaId(rs.getLong("tekstiryhma_id")))
                    )
            ),
            funktiokutsuId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toList())
    ));
  }

  private Map<FunktiokutsuId, Set<Syoteparametri>> readSyoteparametrit(FunktiokutsuId funktiokutsuId) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select syoteparametri.id,\n" +
                    "       version,\n" +
                    "       arvo,\n" +
                    "       avain,\n" +
                    "       funktiokutsu_id\n" +
                    "from tree\n" +
                    "join syoteparametri\n" +
                    "    on syoteparametri.funktiokutsu_id = tree.id",
            (rs, rowNum) -> Pair.of(
                    new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                    new Syoteparametri(
                            new SyoteparametriId(rs.getLong("id")),
                            rs.getLong("version"),
                            rs.getString("avain"),
                            rs.getString("arvo"))
            ),
            funktiokutsuId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toSet())
    ));
  }

  private Map<SyotettavanarvontyyppiId, Syotettavanarvontyyppi> readSyotettavanarvontyypit(FunktiokutsuId funktiokutsuId) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select distinct syotettavanarvonkoodi.id,\n" +
                    "       syotettavanarvonkoodi.version,\n" +
                    "       arvo,\n" +
                    "       nimi_en,\n" +
                    "       nimi_fi,\n" +
                    "       nimi_sv,\n" +
                    "       uri\n" +
                    "from tree\n" +
                    "join valintaperuste_viite as vpv\n" +
                    "    on vpv.funktiokutsu_id = tree.id\n" +
                    "join syotettavanarvonkoodi\n" +
                    "    on syotettavanarvonkoodi.id = vpv.syotettavanarvontyyppi_id",
            (rs, rowNum) -> new Syotettavanarvontyyppi(
                    new SyotettavanarvontyyppiId(rs.getLong("id")),
                    rs.getLong("version"),
                    rs.getString("uri"),
                    rs.getString("nimi_fi"),
                    rs.getString("nimi_sv"),
                    rs.getString("nimi_en"),
                    rs.getString("arvo")
            ),
            funktiokutsuId.id
    ).stream().collect(Collectors.toMap(Syotettavanarvontyyppi::getId, Function.identity()));
  }

  private Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> readFunktioargumentit(FunktiokutsuId rootId) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select funktioargumentti.id,\n" +
                    "       version,\n" +
                    "       indeksi,\n" +
                    "       funktiokutsuchild_id,\n" +
                    "       laskentakaavachild_id,\n" +
                    "       funktiokutsuparent_id\n" +
                    "from tree\n" +
                    "join funktioargumentti\n" +
                    "    on funktioargumentti.funktiokutsuparent_id = tree.id",
            (rs, rowNum) -> {
              long maybeFunktiokutsuchildId = rs.getLong("funktiokutsuchild_id");
              FunktiokutsuId funktiokutsuId = rs.wasNull() ? null : new FunktiokutsuId(maybeFunktiokutsuchildId);
              long maybeLaskentakaavachildId = rs.getLong("laskentakaavachild_id");
              LaskentakaavaId laskentakaavaId = rs.wasNull() ? null : new LaskentakaavaId(maybeLaskentakaavachildId);
              return Pair.of(
                      new FunktiokutsuId(rs.getLong("funktiokutsuparent_id")),
                      new FunktioargumenttiBuilder(
                              new FunktioargumenttiId(rs.getLong("id")),
                              funktiokutsuId,
                              laskentakaavaId,
                              rs.getInt("indeksi")
                      )
              );
            },
            rootId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toList())
    ));
  }

  private Map<FunktiokutsuId, List<ValintaperusteViite>> readValintaperusteViitteet(FunktiokutsuId funktiokutsuId,
                                                                                    Map<TekstiRyhmaId, TekstiRyhma> tekstiryhmat,
                                                                                    Map<SyotettavanarvontyyppiId, Syotettavanarvontyyppi> syotettavanarvontyypit) {
    return new JdbcTemplate(this.dataSource).query(
            "" +
                    "with recursive " + FUNKTIOKUTSU_TREE_CTE + "\n" +
                    "select valintaperuste_viite.id,\n" +
                    "       version,\n" +
                    "       kuvaus,\n" +
                    "       lahde,\n" +
                    "       on_pakollinen,\n" +
                    "       tunniste,\n" +
                    "       funktiokutsu_id,\n" +
                    "       epasuora_viittaus,\n" +
                    "       indeksi,\n" +
                    "       tekstiryhma_id,\n" +
                    "       vaatii_osallistumisen,\n" +
                    "       syotettavissa_kaikille,\n" +
                    "       syotettavanarvontyyppi_id,\n" +
                    "       tilastoidaan\n" +
                    "from tree\n" +
                    "join valintaperuste_viite\n" +
                    "    on valintaperuste_viite.funktiokutsu_id = tree.id",
            (rs, rowNum) -> {
              long maybeSyotettavanarvontyyppiId = rs.getLong("syotettavanarvontyyppi_id");
              Syotettavanarvontyyppi syotettavanarvontyyppi = rs.wasNull() ?
                      null :
                      syotettavanarvontyypit.get(new SyotettavanarvontyyppiId(maybeSyotettavanarvontyyppiId));
              return Pair.of(
                      new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                      new ValintaperusteViite(
                              new ValintaperusteViiteId(rs.getLong("id")),
                              rs.getLong("version"),
                              rs.getString("tunniste"),
                              rs.getString("kuvaus"),
                              Valintaperustelahde.valueOf(rs.getString("lahde")),
                              rs.getBoolean("on_pakollinen"),
                              rs.getBoolean("epasuora_viittaus"),
                              rs.getInt("indeksi"),
                              tekstiryhmat.get(new TekstiRyhmaId(rs.getLong("tekstiryhma_id"))),
                              rs.getBoolean("vaatii_osallistumisen"),
                              rs.getBoolean("syotettavissa_kaikille"),
                              syotettavanarvontyyppi,
                              rs.getBoolean("tilastoidaan")
                      )
              );
            },
            funktiokutsuId.id
    ).stream().collect(Collectors.groupingBy(
            Pair::getLeft,
            Collectors.mapping(Pair::getRight, Collectors.toList())
    ));
  }

  private Funktiokutsu read(FunktiokutsuId id) {
    Map<FunktiokutsuId, FunktiokutsuBuilder> funktiokutsuBuilders = readFunktiokutsut(id);
    Map<TekstiRyhmaId, Set<LokalisoituTeksti>> lokalisoidutTekstit = readLokalisoidutTekstit(id);
    Map<TekstiRyhmaId, TekstiRyhma> tekstiRyhmat = readTekstiryhmat(id, lokalisoidutTekstit);
    Map<FunktiokutsuId, Set<Arvokonvertteriparametri>> arvokonvertteriparametrit = readArvokonvertteriparametrit(id, tekstiRyhmat);
    Map<FunktiokutsuId, List<Arvovalikonvertteriparametri>> arvovalikonvertteriparametrit = readArvovalikonvertteriparametrit(id, tekstiRyhmat);
    Map<FunktiokutsuId, Set<Syoteparametri>> syoteparametrit = readSyoteparametrit(id);
    Map<SyotettavanarvontyyppiId, Syotettavanarvontyyppi> syotettavanarvontyypit = readSyotettavanarvontyypit(id);
    Map<FunktiokutsuId, List<FunktioargumenttiBuilder>> funktioargumenttiBuilders = readFunktioargumentit(id);
    Map<FunktiokutsuId, List<ValintaperusteViite>> valintaperusteViitteet = readValintaperusteViitteet(id, tekstiRyhmat, syotettavanarvontyypit);
    Map<LaskentakaavaId, Laskentakaava> laskentakaavat = funktioargumenttiBuilders.entrySet().stream()
            .flatMap(e -> e.getValue().stream())
            .flatMap(fa -> fa.laskentakaavaId == null ? Stream.empty() : Stream.of(fa.laskentakaavaId))
            .distinct()
            .collect(Collectors.toMap(Function.identity(), this::read));

    return funktiokutsuBuilders.get(id).build(
            funktiokutsuBuilders,
            laskentakaavat,
            arvokonvertteriparametrit,
            arvovalikonvertteriparametrit,
            syoteparametrit,
            funktioargumenttiBuilders,
            valintaperusteViitteet
    );
  }

  @Override
  public Laskentakaava insert(LaskentakaavaCreateDTO dto,
                              LaskentakaavaId kopioLaskentakaavasta,
                              ValintaryhmaId valintaryhma,
                              HakukohdeViiteId hakukohdeViite) {
    Funktiokutsu funktiokutsu = this.insert(dto.getFunktiokutsu(), dto.getNimi());
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    new NamedParameterJdbcTemplate(this.dataSource).update(
            "" +
                    "insert into laskentakaava (\n" +
                    "    id,\n" +
                    "    version,\n" +
                    "    kuvaus,\n" +
                    "    nimi,\n" +
                    "    on_luonnos,\n" +
                    "    tyyppi,\n" +
                    "    funktiokutsu_id,\n" +
                    "    hakukohdeviite,\n" +
                    "    valintaryhmaviite,\n" +
                    "    kopio_laskentakaavasta_id" +
                    ")\n" +
                    "values (\n" +
                    "    nextval('hibernate_sequence'),\n" +
                    "    :version,\n" +
                    "    :kuvaus,\n" +
                    "    :nimi,\n" +
                    "    :on_luonnos,\n" +
                    "    :tyyppi,\n" +
                    "    :funktiokutsu_id,\n" +
                    "    :hakukohdeviite,\n" +
                    "    :valintaryhmaviite,\n" +
                    "    :kopio_laskentakaavasta_id" +
                    ") returning id",
            new MapSqlParameterSource()
                    .addValue("version", 0)
                    .addValue("kuvaus", dto.getKuvaus())
                    .addValue("nimi", dto.getNimi())
                    .addValue("on_luonnos", dto.getOnLuonnos() == null ? false : dto.getOnLuonnos())
                    .addValue("tyyppi", funktiokutsu.getFunktionimi().getTyyppi().name())
                    .addValue("funktiokutsu_id", funktiokutsu.getId().id)
                    .addValue("hakukohdeviite", hakukohdeViite == null ? null : hakukohdeViite.id)
                    .addValue("valintaryhmaviite", valintaryhma == null ? null : valintaryhma.id)
                    .addValue("kopio_laskentakaavasta_id", kopioLaskentakaavasta == null ? null : kopioLaskentakaavasta.id),
            keyHolder
    );
    LaskentakaavaId id = new LaskentakaavaId((Long) keyHolder.getKeys().get("id"));
    return new Laskentakaava(
            id,
            0,
            dto.getOnLuonnos() == null ? false : dto.getOnLuonnos(),
            dto.getNimi(),
            dto.getKuvaus(),
            kopioLaskentakaavasta,
            valintaryhma,
            hakukohdeViite,
            funktiokutsu
    );
  }

  @Override
  public Laskentakaava read(LaskentakaavaId id) {
    try {
      Pair<FunktiokutsuId, Function<Funktiokutsu, Laskentakaava>> builder = new JdbcTemplate(this.dataSource).queryForObject(
              "" +
                      "select id,\n" +
                      "       version,\n" +
                      "       kuvaus,\n" +
                      "       nimi,\n" +
                      "       on_luonnos,\n" +
                      "       tyyppi,\n" +
                      "       funktiokutsu_id,\n" +
                      "       hakukohdeviite,\n" +
                      "       valintaryhmaviite,\n" +
                      "       kopio_laskentakaavasta_id\n" +
                      "from laskentakaava\n" +
                      "where id = ?",
              (rs, rowNum) -> {
                LaskentakaavaId laskentakaavaId = new LaskentakaavaId(rs.getLong("id"));
                long version = rs.getLong("version");
                boolean onLuonnos = rs.getBoolean("on_luonnos");
                String nimi = rs.getString("nimi");
                String kuvaus = rs.getString("kuvaus");
                long kopioLaskentakaavastaId = rs.getLong("kopio_laskentakaavasta_id");
                LaskentakaavaId kopioLaskentakaavasta = rs.wasNull() ? null : new LaskentakaavaId(kopioLaskentakaavastaId);
                long valintaryhmaviite = rs.getLong("valintaryhmaviite");
                ValintaryhmaId valintaryhma = rs.wasNull() ? null : new ValintaryhmaId(valintaryhmaviite);
                long hakukohdeviite = rs.getLong("hakukohdeviite");
                HakukohdeViiteId hakukohdeViite = rs.wasNull() ? null : new HakukohdeViiteId(hakukohdeviite);
                return Pair.of(
                        new FunktiokutsuId(rs.getLong("funktiokutsu_id")),
                        funktiokutsu -> new Laskentakaava(
                                laskentakaavaId,
                                version,
                                onLuonnos,
                                nimi,
                                kuvaus,
                                kopioLaskentakaavasta,
                                valintaryhma,
                                hakukohdeViite,
                                funktiokutsu
                        )
                );
              },
              id.id
      );
      return builder.getRight().apply(this.read(builder.getLeft()));
    } catch (EmptyResultDataAccessException e) {
      throw new LaskentakaavaEiOleOlemassaException("Laskentakaavaa (" + id.id + ") ei ole " + "olemassa", id.id);
    }
  }

  @Override
  @Transactional
  public Laskentakaava update(LaskentakaavaId id, LaskentakaavaCreateDTO dto) {
    Funktiokutsu uusiFunktiokutsu = this.insert(dto.getFunktiokutsu(), dto.getNimi());
    NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
    Long vanhaFunktiokutsuId = jdbcTemplate.queryForObject(
            "" +
                    "update laskentakaava\n" +
                    "set kuvaus = :kuvaus,\n" +
                    "    nimi = :nimi,\n" +
                    "    on_luonnos = :on_luonnos,\n" +
                    "    tyyppi = :tyyppi,\n" +
                    "    funktiokutsu_id = :funktiokutsu_id\n" +
                    "from laskentakaava as old\n" +
                    "where laskentakaava.id = old.id and\n" +
                    "      laskentakaava.id = :id\n" +
                    "returning old.funktiokutsu_id",
            new MapSqlParameterSource()
                    .addValue("id", id.id)
                    .addValue("kuvaus", dto.getKuvaus())
                    .addValue("nimi", dto.getNimi())
                    .addValue("on_luonnos", dto.getOnLuonnos() == null ? false : dto.getOnLuonnos())
                    .addValue("tyyppi", dto.getFunktiokutsu().getFunktionimi().getTyyppi().name())
                    .addValue("funktiokutsu_id", uusiFunktiokutsu.getId().id),
            Long.class
    );
    if (vanhaFunktiokutsuId == null) {
      throw new LaskentakaavaEiOleOlemassaException("Laskentakaava (" + id.id + ") ei ole olemassa.", id.id);
    }
    jdbcTemplate.update(
            "delete from funktiokutsu where id = :id",
            new MapSqlParameterSource().addValue("id", vanhaFunktiokutsuId)
    );
    return this.read(id);
  }

  @Override
  public boolean delete(LaskentakaavaId id) {
    try {
      return new JdbcTemplate(this.dataSource).update("delete from laskentakaava where id = ?", id.id) > 0;
    } catch (DataIntegrityViolationException e) {
      return false;
    }
  }

  @Override
  public List<Laskentakaava> findKaavas(boolean all,
                                        String valintaryhmaOid,
                                        String hakukohdeOid,
                                        Funktiotyyppi tyyppi) {
    return new NamedParameterJdbcTemplate(this.dataSource).queryForList(
            "" +
                    "select lk.id\n" +
                    "from laskentakaava as lk\n" +
                    "join funktiokutsu as fk\n" +
                    "    on fk.id = lk.funktiokutsu_id\n" +
                    "left join hakukohde_viite as hkv\n" +
                    "    on hkv.id = lk.hakukohdeviite\n" +
                    "left join valintaryhma as vr\n" +
                    "    on vr.id = lk.valintaryhmaviite\n" +
                    "where (:luonnos or not on_luonnos) and\n" +
                    "      (:tyyppi::varchar is null or tyyppi = :tyyppi) and\n" +
                    "      hkv.oid is not distinct from :hakukohde and\n" +
                    "      vr.oid is not distinct from :valintaryhma",
            new MapSqlParameterSource()
                    .addValue("hakukohde", hakukohdeOid)
                    .addValue("valintaryhma", valintaryhmaOid)
                    .addValue("luonnos", all)
                    .addValue("tyyppi", tyyppi == null ? null : tyyppi.name()),
            Long.class
    ).stream().map(id -> this.read(new LaskentakaavaId(id))).collect(Collectors.toList());
  }

  @Override
  public List<Laskentakaava> findHakukohteenKaavat(String hakukohdeOid) {
    return new JdbcTemplate(this.dataSource).queryForList(
            "" +
                    "select lk.id\n" +
                    "from laskentakaava as lk\n" +
                    "join jarjestyskriteeri as jk\n" +
                    "    on jk.laskentakaava_id = lk.id\n" +
                    "join valintatapajono as vtj\n" +
                    "    on vtj.id = jk.valintatapajono_id\n" +
                    "join valinnan_vaihe as vv\n" +
                    "    on vv.id = vtj.valinnan_vaihe_id\n" +
                    "join hakukohde_viite as hv\n" +
                    "    on hv.id = vv.hakukohde_viite_id\n" +
                    "where hv.oid = ?",
            Long.class,
            hakukohdeOid
    ).stream().map(id -> this.read(new LaskentakaavaId(id))).collect(Collectors.toList());
  }

  @Override
  public Laskentakaava etsiKaavaTaiSenKopio(LaskentakaavaId laskentakaavaId, HakukohdeViiteId hakukohdeViiteId, ValintaryhmaId valintaryhmaId) {
    List<Long> ids = new NamedParameterJdbcTemplate(this.dataSource).queryForList(
            "" +
                    "with recursive ryhma(id) as (\n" +
                    "    (select valintaryhma_id\n" +
                    "     from hakukohde_viite\n" +
                    "     where :hakukohde_viite_id::bigint is not null and\n" +
                    "           id = :hakukohde_viite_id and\n" +
                    "           valintaryhma_id is not null\n" +
                    "     union\n" +
                    "     select :valintaryhma_id\n" +
                    "     where :valintaryhma_id::bigint is not null)\n" +
                    "    union all\n" +
                    "    select parent_id\n" +
                    "    from ryhma\n" +
                    "    join valintaryhma\n" +
                    "      on valintaryhma.id = ryhma.id\n" +
                    "    where parent_id is not null\n" +
                    ")\n" +
                    "select id\n" +
                    "from laskentakaava\n" +
                    "where :hakukohde_viite_id::bigint is not null and\n" +
                    "      hakukohdeviite is not null and\n" +
                    "      hakukohdeviite = :hakukohde_viite_id and\n" +
                    "      (id = :id or kopio_laskentakaavasta_id = :id)\n" +
                    "union all\n" +
                    "select id\n" +
                    "from laskentakaava\n" +
                    "where valintaryhmaviite in (select id from ryhma) and\n" +
                    "      (id = :id or kopio_laskentakaavasta_id = :id)",
            new MapSqlParameterSource()
                    .addValue("id", laskentakaavaId.id)
                    .addValue("hakukohde_viite_id", hakukohdeViiteId == null ? null : hakukohdeViiteId.id)
                    .addValue("valintaryhma_id", valintaryhmaId == null ? null : valintaryhmaId.id),
            Long.class
    );
    return ids.isEmpty() ? null : this.read(new LaskentakaavaId(ids.get(0)));
  }

  @Override
  public List<LaskentakaavaId> irrotaHakukohteesta(HakukohdeViiteId hakukohdeViiteId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    new NamedParameterJdbcTemplate(this.dataSource).update(
            "" +
                    "update laskentakaava\n" +
                    "set hakukohdeviite = null\n" +
                    "where hakukohdeviite = :hakukohdeviite\n" +
                    "returning id",
            new MapSqlParameterSource().addValue("hakukohdeviite", hakukohdeViiteId.id),
            keyHolder
    );
    return keyHolder.getKeyList().stream()
            .map(m -> new LaskentakaavaId((Long) m.get("id")))
            .collect(Collectors.toList());
  }

  @Override
  public void liitaHakukohteeseen(HakukohdeViiteId hakukohdeViiteId, List<LaskentakaavaId> laskentakaavaIds) {
    new NamedParameterJdbcTemplate(this.dataSource).update(
            "" +
                    "update laskentakaava\n" +
                    "set hakukohdeviite = :hakukohdeviite\n" +
                    "where id in (:ids)",
            new MapSqlParameterSource()
                    .addValue("hakukohdeviite", hakukohdeViiteId.id)
                    .addValue(
                            "ids",
                            laskentakaavaIds.stream().map(i -> i.id).collect(Collectors.toList())
                    )
    );
  }
}
