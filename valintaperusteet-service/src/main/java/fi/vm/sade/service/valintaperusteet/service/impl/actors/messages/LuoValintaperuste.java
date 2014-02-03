package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class LuoValintaperuste {

    private String arvo;
    private String uri;
    private String nimi;
    private String nimiSv;
    private String nimiEn;
    private String pOid;
    private String lOid;
    private Laskentakaava pkPeruskaava;
    private Laskentakaava[] pkTasasijakriteerit;
    private Laskentakaava lkPeruskaava;
    private Laskentakaava[] lkTasasijakriteerit;

    private Laskentakaava kielikoelaskentakaava;


    public LuoValintaperuste(String arvo, String uri, String nimi, String nimiSv, String nimiEn,
                             String pOid, String lOid, Laskentakaava pkPeruskaava,
                             Laskentakaava[] pkTasasijakriteerit, Laskentakaava lkPeruskaava,
                             Laskentakaava[] lkTasasijakriteerit, Laskentakaava kielikoelaskentakaava) {
        this.arvo = arvo;
        this.uri = uri;
        this.nimi = nimi;
        this.nimiSv = nimiSv;
        this.nimiEn = nimiEn;
        this.pOid = pOid;
        this.lOid = lOid;
        this.pkPeruskaava = pkPeruskaava;
        this.pkTasasijakriteerit = pkTasasijakriteerit;
        this.lkPeruskaava = lkPeruskaava;
        this.lkTasasijakriteerit = lkTasasijakriteerit;
        this.kielikoelaskentakaava = kielikoelaskentakaava;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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

    public String getpOid() {
        return pOid;
    }

    public void setpOid(String pOid) {
        this.pOid = pOid;
    }

    public String getlOid() {
        return lOid;
    }

    public void setlOid(String lOid) {
        this.lOid = lOid;
    }

    public Laskentakaava getPkPeruskaava() {
        return pkPeruskaava;
    }

    public void setPkPeruskaava(Laskentakaava pkPeruskaava) {
        this.pkPeruskaava = pkPeruskaava;
    }

    public Laskentakaava[] getPkTasasijakriteerit() {
        return pkTasasijakriteerit;
    }

    public void setPkTasasijakriteerit(Laskentakaava[] pkTasasijakriteerit) {
        this.pkTasasijakriteerit = pkTasasijakriteerit;
    }

    public Laskentakaava getLkPeruskaava() {
        return lkPeruskaava;
    }

    public void setLkPeruskaava(Laskentakaava lkPeruskaava) {
        this.lkPeruskaava = lkPeruskaava;
    }

    public Laskentakaava[] getLkTasasijakriteerit() {
        return lkTasasijakriteerit;
    }

    public void setLkTasasijakriteerit(Laskentakaava[] lkTasasijakriteerit) {
        this.lkTasasijakriteerit = lkTasasijakriteerit;
    }

    public Laskentakaava getKielikoelaskentakaava() {
        return kielikoelaskentakaava;
    }

    public void setKielikoelaskentakaava(Laskentakaava kielikoelaskentakaava) {
        this.kielikoelaskentakaava = kielikoelaskentakaava;
    }
}
