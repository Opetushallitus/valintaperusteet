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
@Table(name = "hakukohdekoodi")
@Cacheable(true)
public class Hakukohdekoodi extends Koodi {

    @ManyToMany(mappedBy = "hakukohdekoodit")
    private Set<Valintaryhma> valintaryhmat = new HashSet<Valintaryhma>();

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

    public Set<Valintaryhma> getValintaryhmat() {
        return valintaryhmat;
    }

    public void setValintaryhmat(Set<Valintaryhma> valintaryhmat) {
        this.valintaryhmat = valintaryhmat;
    }
}