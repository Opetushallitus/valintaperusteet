package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ValintaryhmaDAOImpl extends AbstractJpaDAOImpl<Valintaryhma, Long>
    implements ValintaryhmaDAO {

  @Autowired
  private DataSource dataSource;

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  @Override
  public List<Valintaryhma> findChildrenByParentOid(String id) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

    BooleanExpression eq = null;
    if (id == null) {
      eq = valintaryhma.ylavalintaryhma.isNull();
    } else {
      eq = valintaryhma.ylavalintaryhma.oid.eq(id);
    }
    return from(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetch()
        .leftJoin(valintaryhma.hakukohdeViitteet)
        .fetch()
        .leftJoin(valintaryhma.hakukohdekoodit)
        .fetch()
        .leftJoin(valintaryhma.valintakoekoodit)
        .fetch()
        .leftJoin(valintaryhma.organisaatiot)
        .fetch()
        .where(eq)
        .distinct()
        .orderBy(valintaryhma.nimi.asc())
        .list(valintaryhma);
  }

  @Override
  public List<Valintaryhma> findChildrenByParentOidPlain(String oid) {
    if (oid == null) {
      return new ArrayList<>();
    }
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return from(valintaryhma)
        .where(valintaryhma.ylavalintaryhma.oid.eq(oid))
        .distinct()
        .list(valintaryhma);
  }

  @Override
  public Valintaryhma readByOid(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    Valintaryhma vr =
        from(valintaryhma)
            .leftJoin(valintaryhma.alavalintaryhmat)
            .fetch()
            .leftJoin(valintaryhma.hakukohdeViitteet)
            .fetch()
            .leftJoin(valintaryhma.hakukohdekoodit)
            .fetch()
            .leftJoin(valintaryhma.organisaatiot)
            .fetch()
            .where(valintaryhma.oid.eq(oid))
            .singleResult(valintaryhma);
    if (vr != null) {
      vr.getValintakoekoodit().size();
    }
    return vr;
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
    return from(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetch()
        .leftJoin(valintaryhma.ylavalintaryhma)
        .fetch()
        .where(valintaryhma.oid.eq(oid))
        .singleResult(valintaryhma);
  }

  @Override
  public List<Valintaryhma> findAllFetchAlavalintaryhmat() {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return from(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetch()
        .leftJoin(valintaryhma.organisaatiot)
        .fetch()
        .distinct()
        .list(valintaryhma);
  }

  @Override
  public Valintaryhma findAllFetchAlavalintaryhmat(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return from(valintaryhma)
        .leftJoin(valintaryhma.alavalintaryhmat)
        .fetch()
        .leftJoin(valintaryhma.organisaatiot)
        .fetch()
        .where(valintaryhma.oid.eq(oid))
        .singleResult(valintaryhma);
  }

  @Override
  public List<Valintaryhma> haeHakukohdekoodinJaValintakoekoodienMukaan(
      String hakuOid, String hakukohdekoodiUri, Set<String> valintakoekoodiUrit) {
    List<String> oids = new NamedParameterJdbcTemplate(this.dataSource).queryForList(
        "" +
            "with recursive haun_ryhmat(id) as (\n" +
            "    select id\n" +
            "    from valintaryhma\n" +
            "    where hakuoid = :haku_oid\n" +
            "    union all\n" +
            "    select vr.id\n" +
            "    from haun_ryhmat\n" +
            "    join valintaryhma as vr\n" +
            "        on vr.parent_id = haun_ryhmat.id" +
            ")\n" +
            "select \"oid\"\n" +
            "from valintaryhma as vr\n" +
            "join valintaryhma_hakukohdekoodi as vr_hkk\n" +
            "    on vr_hkk.valintaryhma_id = vr.id\n" +
            "join hakukohdekoodi as hkk\n" +
            "    on hkk.id = vr_hkk.hakukohdekoodi_id\n" +
            "where vr.id in (select id from haun_ryhmat) and\n" +
            "      hkk.uri = :hakukohdekoodi and\n" +
            (valintakoekoodiUrit.isEmpty() ?
            "      not exists (select uri\n" +
            "                  from valintakoekoodi as vkk\n" +
            "                  join valintaryhma_valintakoekoodi as vr_vkk\n" +
            "                      on vr_vkk.valintakoekoodi_id = vkk.id\n" +
            "                  where vr_vkk.valintaryhma_id = vr.id)" :
            "      not exists (select uri\n" +
            "                  from valintakoekoodi as vkk\n" +
            "                  join valintaryhma_valintakoekoodi as vr_vkk\n" +
            "                      on vr_vkk.valintakoekoodi_id = vkk.id\n" +
            "                  where vr_vkk.valintaryhma_id = vr.id and\n" +
            "                        vkk.uri not in (:valintakoekoodit)) and" +
            "      not exists (select uri\n" +
            "                  from unnest(array[:valintakoekoodit ]) as t(uri)\n" +
            "                  except\n" +
            "                  select uri\n" +
            "                  from valintakoekoodi as vkk\n" +
            "                  join valintaryhma_valintakoekoodi as vr_vkk\n" +
            "                      on vr_vkk.valintakoekoodi_id = vkk.id\n" +
            "                  where vr_vkk.valintaryhma_id = vr.id)"),
        new MapSqlParameterSource()
            .addValue("haku_oid", hakuOid)
            .addValue("hakukohdekoodi", hakukohdekoodiUri)
            .addValue("valintakoekoodit", valintakoekoodiUrit),
        String.class
    );
    return oids.stream().map(this::readByOid).collect(Collectors.toList());
  }

  private Valintaryhma findParent(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    Valintaryhma current =
        from(valintaryhma).where(valintaryhma.oid.eq(oid)).singleResult(valintaryhma);
    return current.getYlavalintaryhma();
  }

  @Override
  public List<Valintaryhma> readByHakukohdekoodiUri(String koodiUri) {
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    QValintaryhma vr = QValintaryhma.valintaryhma;
    return from(vr)
        .innerJoin(vr.hakukohdekoodit, koodi)
        .where(koodi.uri.eq(koodiUri))
        .distinct()
        .list(vr);
  }

  @Override
  public List<Valintaryhma> readByHakuoid(String hakuoid) {
    QValintaryhma vr = QValintaryhma.valintaryhma;

    return from(vr).where(vr.hakuoid.eq(hakuoid)).distinct().list(vr);
  }
}
