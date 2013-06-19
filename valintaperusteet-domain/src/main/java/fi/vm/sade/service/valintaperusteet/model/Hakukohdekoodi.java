package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * User: wuoti
 * Date: 7.5.2013
 * Time: 12.55
 */
@Entity
@Table(name = "hakukohdekoodi")
public class Hakukohdekoodi extends Koodi {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohdekoodi")
    private Set<HakukohdeViite> hakukohteet = new HashSet<HakukohdeViite>();

    public Set<HakukohdeViite> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(Set<HakukohdeViite> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }

    public void addHakukohde(HakukohdeViite hakukohdeViite) {
        this.hakukohteet.add(hakukohdeViite);
        hakukohdeViite.setHakukohdekoodi(this);
    }

}