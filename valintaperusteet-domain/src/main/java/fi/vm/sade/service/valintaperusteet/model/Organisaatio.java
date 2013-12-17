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

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @Column(name = "parent_oid_path", nullable = false, unique = true)
    private String parentOidPath;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getParentOidPath() {
        return parentOidPath;
    }

    public void setParentOidPath(String parentOidPath) {
        this.parentOidPath = parentOidPath;
    }

    public Set<Valintaryhma> getValintaryhmat() {
        return valintaryhmat;
    }

    public void setValintaryhmat(Set<Valintaryhma> valintaryhmat) {
        this.valintaryhmat = valintaryhmat;
    }
}
