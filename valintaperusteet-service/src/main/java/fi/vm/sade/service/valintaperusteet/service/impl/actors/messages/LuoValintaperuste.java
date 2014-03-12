package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class LuoValintaperuste {

    private KoodiDTO hakukohdekoodi;
    private String pOid;
    private String lOid;
    private Laskentakaava pkPeruskaava;
    private Laskentakaava[] pkTasasijakriteerit;
    private Laskentakaava lkPeruskaava;
    private Laskentakaava[] lkTasasijakriteerit;

    private Laskentakaava kielikoelaskentakaava;
    private Laskentakaava lisapisteLaskentakaava;


    public LuoValintaperuste(KoodiDTO hakukohdekoodi,
                             String pOid, String lOid, Laskentakaava pkPeruskaava,
                             Laskentakaava[] pkTasasijakriteerit, Laskentakaava lkPeruskaava,
                             Laskentakaava[] lkTasasijakriteerit, Laskentakaava kielikoelaskentakaava,
                             Laskentakaava lisapisteLaskentakaava) {
        this.hakukohdekoodi = hakukohdekoodi;
        this.pOid = pOid;
        this.lOid = lOid;
        this.pkPeruskaava = pkPeruskaava;
        this.pkTasasijakriteerit = pkTasasijakriteerit;
        this.lkPeruskaava = lkPeruskaava;
        this.lkTasasijakriteerit = lkTasasijakriteerit;
        this.kielikoelaskentakaava = kielikoelaskentakaava;
        this.lisapisteLaskentakaava = lisapisteLaskentakaava;
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

    public Laskentakaava getLisapisteLaskentakaava() {
        return lisapisteLaskentakaava;
    }

    public void setLisapisteLaskentakaava(Laskentakaava lisapisteLaskentakaava) {
        this.lisapisteLaskentakaava = lisapisteLaskentakaava;
    }

    public KoodiDTO getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(KoodiDTO hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }
}
