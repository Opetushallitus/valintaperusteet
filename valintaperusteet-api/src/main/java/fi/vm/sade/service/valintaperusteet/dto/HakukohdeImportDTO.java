package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

/**
 * Created by jukais on 4.3.2014.
 */
@ApiModel(value = "HakukohdeImportDTO", description = "Hakukohteen lisääminen")
public class HakukohdeImportDTO {
    private String hakukohdeOid;
    private HakukohdekoodiDTO hakukohdekoodi;
    private String tarjoajaOid;
    private int valinnanAloituspaikat;
    private List<MonikielinenTekstiDTO> tarjoajaNimi = new ArrayList<MonikielinenTekstiDTO>();
    private List<MonikielinenTekstiDTO> hakukohdeNimi = new ArrayList<MonikielinenTekstiDTO>();
    private List<MonikielinenTekstiDTO> hakuKausi = new ArrayList<MonikielinenTekstiDTO>();
    private String hakuVuosi;
    private String hakuOid;
    private String tila;
    private List<HakukohteenValintakoeDTO> valintakoe = new ArrayList<HakukohteenValintakoeDTO>();
    private List<AvainArvoDTO> valintaperuste = new ArrayList<AvainArvoDTO>();
    private String haunkohdejoukkoUri;

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }


    public HakukohdekoodiDTO getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(HakukohdekoodiDTO hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public int getValinnanAloituspaikat() {
        return valinnanAloituspaikat;
    }

    public void setValinnanAloituspaikat(int valinnanAloituspaikat) {
        this.valinnanAloituspaikat = valinnanAloituspaikat;
    }


    public List<MonikielinenTekstiDTO> getTarjoajaNimi() {
        return tarjoajaNimi;
    }

    public void setTarjoajaNimi(List<MonikielinenTekstiDTO> tarjoajaNimi) {
        this.tarjoajaNimi = tarjoajaNimi;
    }

    public List<MonikielinenTekstiDTO> getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public void setHakukohdeNimi(List<MonikielinenTekstiDTO> hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    public List<MonikielinenTekstiDTO> getHakuKausi() {
        return hakuKausi;
    }

    public void setHakuKausi(List<MonikielinenTekstiDTO> hakuKausi) {
        this.hakuKausi = hakuKausi;
    }


    public String getHakuVuosi() {
        return hakuVuosi;
    }

    public void setHakuVuosi(String hakuVuosi) {
        this.hakuVuosi = hakuVuosi;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public List<HakukohteenValintakoeDTO> getValintakoe() {
        return valintakoe;
    }

    public void setValintakoe(List<HakukohteenValintakoeDTO> valintakoe) {
        this.valintakoe = valintakoe;
    }

    public List<AvainArvoDTO> getValintaperuste() {
        return valintaperuste;
    }

    public void setValintaperuste(List<AvainArvoDTO> valintaperuste) {
        this.valintaperuste = valintaperuste;
    }

    public String getHaunkohdejoukkoUri() {
        return haunkohdejoukkoUri;
    }

    public void setHaunkohdejoukkoUri(String haunkohdejoukkoUri) {
        this.haunkohdejoukkoUri = haunkohdejoukkoUri;
    }
}
