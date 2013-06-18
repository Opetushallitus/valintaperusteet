package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * User: wuoti
 * Date: 18.6.2013
 * Time: 13.31
 */
@Entity
@DiscriminatorValue("valintakoekoodi")
public class Valintakoekoodi extends Koodi {

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "valintakokeet")
    private Set<HakukohdeViite> hakukohteet;

    public Set<HakukohdeViite> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(Set<HakukohdeViite> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }
}
