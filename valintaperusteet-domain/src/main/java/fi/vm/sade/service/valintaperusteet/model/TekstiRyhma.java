package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: kwuoti
 * Date: 21.2.2013
 * Time: 9.21
 */
@Entity
@Table(name = "tekstiryhma")
@Cacheable(true)
public class TekstiRyhma extends BaseEntity {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ryhma", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<LokalisoituTeksti> tekstit = new HashSet<LokalisoituTeksti>();

    public Set<LokalisoituTeksti> getTekstit() {
        return tekstit;
    }

    public void setTekstit(Set<LokalisoituTeksti> tekstit) {
        this.tekstit = tekstit;
    }
}
