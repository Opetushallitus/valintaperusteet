package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

/**
 * User: wuoti
 * Date: 18.6.2013
 * Time: 13.41
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "koodityyppi")
@Table(name = "koodi")
public abstract class Koodi extends BaseEntity {

    @JsonView(JsonViews.Basic.class)
    @Column(name = "uri", nullable = false)
    private String uri;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_fi")
    private String nimiFi;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_sv")
    private String nimiSv;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_en")
    private String nimiEn;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "arvo")
    private String arvo;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNimiFi() {
        return nimiFi;
    }

    public void setNimiFi(String nimiFi) {
        this.nimiFi = nimiFi;
    }

    public String getNimiSv() {
        return nimiSv;
    }

    public void setNimiSv(String nimiSv) {
        this.nimiSv = nimiSv;
    }

    public String getNimiEn() {
        return nimiEn;
    }

    public void setNimiEn(String nimiEn) {
        this.nimiEn = nimiEn;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }
}
