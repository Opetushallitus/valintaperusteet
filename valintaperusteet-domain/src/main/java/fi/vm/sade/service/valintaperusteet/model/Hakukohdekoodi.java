package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: wuoti
 * Date: 7.5.2013
 * Time: 12.55
 */
@Entity
@DiscriminatorValue("hakukohdekoodi")
public class Hakukohdekoodi extends Koodi {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohdekoodi")
    private Set<HakukohdeViite> hakukohteet = new HashSet<HakukohdeViite>();

    @JoinColumn(name = "valintaryhma_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Valintaryhma valintaryhma;

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

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
