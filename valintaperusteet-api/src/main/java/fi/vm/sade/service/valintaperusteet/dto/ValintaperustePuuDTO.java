package fi.vm.sade.service.valintaperusteet.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ValintaperustePuuDTO {
    private ValintaperustePuuDTO ylavalintaryhma;

    private Set<ValintaperustePuuDTO> alavalintaryhmat = new HashSet<ValintaperustePuuDTO>();

    private Set<ValintaperustePuuDTO> hakukohdeViitteet = new HashSet<ValintaperustePuuDTO>();

    private String hakuOid;

    private String oid;

    private String tarjoajaOid;

    private String nimi;

    private String tila;

    private ValintaperustePuuTyyppi tyyppi;

    private String kohdejoukko;

    private String hakuvuosi;

    private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

    private OrganisaatioDTO vastuuorganisaatio;

    private Date viimeinenKaynnistyspaiva;

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public ValintaperustePuuDTO getYlavalintaryhma() {
        return ylavalintaryhma;
    }

    public void setYlavalintaryhma(ValintaperustePuuDTO ylavalintaryhma) {
        this.ylavalintaryhma = ylavalintaryhma;
    }

    public Set<ValintaperustePuuDTO> getAlavalintaryhmat() {
        return alavalintaryhmat;
    }

    public void setAlavalintaryhmat(Set<ValintaperustePuuDTO> alavalintaryhmat) {
        this.alavalintaryhmat = alavalintaryhmat;
    }

    public Set<ValintaperustePuuDTO> getHakukohdeViitteet() {
        return hakukohdeViitteet;
    }

    public void setHakukohdeViitteet(Set<ValintaperustePuuDTO> hakukohdeViitteet) {
        this.hakukohdeViitteet = hakukohdeViitteet;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public ValintaperustePuuTyyppi getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(ValintaperustePuuTyyppi tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Set<OrganisaatioDTO> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<OrganisaatioDTO> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }

    public OrganisaatioDTO getVastuuorganisaatio() {
        return vastuuorganisaatio;
    }

    public void setVastuuorganisaatio(OrganisaatioDTO vastuuorganisaatio) {
        this.vastuuorganisaatio = vastuuorganisaatio;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public String getHakuvuosi() {
        return hakuvuosi;
    }

    public void setHakuvuosi(String hakuvuosi) {
        this.hakuvuosi = hakuvuosi;
    }

    public Date getViimeinenKaynnistyspaiva() {
        return viimeinenKaynnistyspaiva;
    }

    public void setViimeinenKaynnistyspaiva(Date viimeinenKaynnistyspaiva) {
        this.viimeinenKaynnistyspaiva = viimeinenKaynnistyspaiva;
    }
}
