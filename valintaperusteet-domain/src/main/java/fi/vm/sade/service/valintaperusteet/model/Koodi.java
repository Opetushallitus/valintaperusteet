package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Koodi extends BaseEntity {
    @Column(name = "uri", nullable = false, unique = true)
    private String uri;

    @Column(name = "nimi_fi")
    private String nimiFi;

    @Column(name = "nimi_sv")
    private String nimiSv;

    @Column(name = "nimi_en")
    private String nimiEn;

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
