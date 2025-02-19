package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ValintaryhmaDAOImpl extends AbstractJpaDAOImpl<Valintaryhma, Long>
    implements ValintaryhmaDAO {

  @Autowired private DataSource dataSource;

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<Valintaryhma> findChildrenByParentOid(String id) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.hakukohdeViitteet)
        .fetchJoin()
        .leftJoin(valintaryhma.hakukohdekoodit)
        .fetchJoin()
        .leftJoin(valintaryhma.valintakoekoodit)
        .fetchJoin()
        .leftJoin(valintaryhma.organisaatiot)
        .fetchJoin()
        .where(
            id == null
                ? valintaryhma.ylavalintaryhma.isNull()
                : valintaryhma.ylavalintaryhma.oid.eq(id))
        .distinct()
        .orderBy(valintaryhma.nimi.asc())
        .fetch();
  }

  @Override
  public List<Valintaryhma> findChildrenByParentOidPlain(String oid) {
    if (oid == null) {
      return new ArrayList<>();
    }
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .where(valintaryhma.ylavalintaryhma.oid.eq(oid))
        .distinct()
        .fetch();
  }

  @Override
  public Valintaryhma readByOid(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.hakukohdeViitteet)
        .fetchJoin()
        .leftJoin(valintaryhma.hakukohdekoodit)
        .fetchJoin()
        .leftJoin(valintaryhma.organisaatiot)
        .fetchJoin()
        .leftJoin(valintaryhma.valintakoekoodit)
        .fetchJoin()
        .where(valintaryhma.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<Valintaryhma> readHierarchy(String childOid) {
    List<Valintaryhma> set = new LinkedList<Valintaryhma>();
    Valintaryhma current = readPlainByOid(childOid);
    set.add(current);
    current = current.getYlavalintaryhma();
    do {
      if (current != null) {
        current = readPlainByOid(current.getOid());
        set.add(current);
        current = current.getYlavalintaryhma();
      }
    } while (current != null);
    return set;
  }

  private Valintaryhma readPlainByOid(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.ylavalintaryhma)
        .fetchJoin()
        .where(valintaryhma.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<Valintaryhma> findAllFetchAlavalintaryhmat() {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.organisaatiot)
        .fetchJoin()
        .distinct()
        .fetch();
  }

  @Override
  public Valintaryhma findAllFetchAlavalintaryhmat(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.organisaatiot)
        .fetchJoin()
        .where(valintaryhma.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public Valintaryhma findByHakuOidFetchAlavalintaryhmat(String hakuOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetchJoin()
        .leftJoin(valintaryhma.organisaatiot)
        .fetchJoin()
        .where(valintaryhma.hakuoid.eq(hakuOid))
        .fetchFirst();
  }

  @Override
  public List<Valintaryhma> haeHakukohdekoodinJaValintakoekoodienMukaan(
      String hakuOid, String hakukohdekoodiUri, Set<String> valintakoekoodiUrit) {
    List<String> oids =
        new NamedParameterJdbcTemplate(this.dataSource)
            .queryForList(
                ""
                    + "with recursive haun_ryhmat(id) as (\n"
                    + "    select id\n"
                    + "    from valintaryhma\n"
                    + "    where hakuoid = :haku_oid\n"
                    + "    union all\n"
                    + "    select vr.id\n"
                    + "    from haun_ryhmat\n"
                    + "    join valintaryhma as vr\n"
                    + "        on vr.parent_id = haun_ryhmat.id"
                    + ")\n"
                    + "select \"oid\"\n"
                    + "from valintaryhma as vr\n"
                    + "join valintaryhma_hakukohdekoodi as vr_hkk\n"
                    + "    on vr_hkk.valintaryhma_id = vr.id\n"
                    + "join hakukohdekoodi as hkk\n"
                    + "    on hkk.id = vr_hkk.hakukohdekoodi_id\n"
                    + "where vr.id in (select id from haun_ryhmat) and\n"
                    + "      hkk.uri = :hakukohdekoodi and\n"
                    + (valintakoekoodiUrit.isEmpty()
                        ? "      not exists (select uri\n"
                            + "                  from valintakoekoodi as vkk\n"
                            + "                  join valintaryhma_valintakoekoodi as vr_vkk\n"
                            + "                      on vr_vkk.valintakoekoodi_id = vkk.id\n"
                            + "                  where vr_vkk.valintaryhma_id = vr.id)"
                        : "      not exists (select uri\n"
                            + "                  from valintakoekoodi as vkk\n"
                            + "                  join valintaryhma_valintakoekoodi as vr_vkk\n"
                            + "                      on vr_vkk.valintakoekoodi_id = vkk.id\n"
                            + "                  where vr_vkk.valintaryhma_id = vr.id and\n"
                            + "                        vkk.uri not in (:valintakoekoodit)) and"
                            + "      not exists (select uri\n"
                            + "                  from unnest(array[:valintakoekoodit ]) as t(uri)\n"
                            + "                  except\n"
                            + "                  select uri\n"
                            + "                  from valintakoekoodi as vkk\n"
                            + "                  join valintaryhma_valintakoekoodi as vr_vkk\n"
                            + "                      on vr_vkk.valintakoekoodi_id = vkk.id\n"
                            + "                  where vr_vkk.valintaryhma_id = vr.id)"),
                new MapSqlParameterSource()
                    .addValue("haku_oid", hakuOid)
                    .addValue("hakukohdekoodi", hakukohdekoodiUri)
                    .addValue("valintakoekoodit", valintakoekoodiUrit),
                String.class);
    return oids.stream().map(this::readByOid).collect(Collectors.toList());
  }

  private Valintaryhma findParent(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    Valintaryhma current =
        queryFactory().selectFrom(valintaryhma).where(valintaryhma.oid.eq(oid)).fetchFirst();
    return current.getYlavalintaryhma();
  }

  @Override
  public List<Valintaryhma> readByHakukohdekoodiUri(String koodiUri) {
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    QValintaryhma vr = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(vr)
        .innerJoin(vr.hakukohdekoodit, koodi)
        .where(koodi.uri.eq(koodiUri))
        .distinct()
        .fetch();
  }

  @Override
  public List<Valintaryhma> readByHakuoid(String hakuoid) {
    QValintaryhma vr = QValintaryhma.valintaryhma;

    return queryFactory().selectFrom(vr).where(vr.hakuoid.eq(hakuoid)).distinct().fetch();
  }
}
