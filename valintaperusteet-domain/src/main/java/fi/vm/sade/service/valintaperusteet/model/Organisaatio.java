package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 25.11.2013
 * Time: 16.08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "organisaatio")
@XmlRootElement
@Cacheable(true)
public class Organisaatio extends BaseEntity {
    @ManyToMany(mappedBy = "organisaatiot")
    private Set<Valintaryhma> valintaryhmat = new HashSet<Valintaryhma>();

    @JsonView(JsonViews.Basic.class)
    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Set<Valintaryhma> getValintaryhmat() {
        return valintaryhmat;
    }

    public void setValintaryhmat(Set<Valintaryhma> valintaryhmat) {
        this.valintaryhmat = valintaryhmat;
    }
}
