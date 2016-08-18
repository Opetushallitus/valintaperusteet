package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ValintaperusteetValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValintaperusteetValinnanVaiheDTO extends ValinnanVaiheDTO {

    private int valinnanVaiheJarjestysluku;
    private String valinnanVaiheOid;
    private List<ValintatapajonoJarjestyskriteereillaDTO> valintatapajono;
    private List<ValintakoeDTO> valintakoe;

    public void setValinnanVaiheJarjestysluku(int valinnanVaiheJarjestysluku) {
        this.valinnanVaiheJarjestysluku = valinnanVaiheJarjestysluku;
    }

    public int getValinnanVaiheJarjestysluku() {
        return valinnanVaiheJarjestysluku;
    }

    public void setValinnanVaiheOid(String valinnanVaiheOid) {
        this.valinnanVaiheOid = valinnanVaiheOid;
    }

    public String getValinnanVaiheOid() {
        return valinnanVaiheOid;
    }

    public List<ValintatapajonoJarjestyskriteereillaDTO> getValintatapajono() {
        if (valintatapajono == null) {
            valintatapajono = new ArrayList<ValintatapajonoJarjestyskriteereillaDTO>();
        }
        return valintatapajono;
    }

    public void setValintatapajono(List<ValintatapajonoJarjestyskriteereillaDTO> valintatapajono) {
        this.valintatapajono = valintatapajono;
    }

    public List<ValintakoeDTO> getValintakoe() {
        if (valintakoe == null) {
            valintakoe = new ArrayList<ValintakoeDTO>();
        }
        return valintakoe;
    }

    public void setValintakoe(List<ValintakoeDTO> valintakoe) {
        this.valintakoe = valintakoe;
    }
}
