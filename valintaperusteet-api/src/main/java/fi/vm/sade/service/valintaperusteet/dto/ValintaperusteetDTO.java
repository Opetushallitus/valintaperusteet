package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jukais on 4.3.2014.
 */
public class ValintaperusteetDTO {
    private String hakukohdeOid;

    private String hakuOid;

    private String tarjoajaOid;

    private int viimeinenValinnanvaihe;

    private ValintaperusteetValinnanVaiheDTO valinnanVaihe;

    private List<HakukohteenValintaperusteDTO> hakukohteenValintaperuste = new ArrayList<HakukohteenValintaperusteDTO>();

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setValinnanVaihe(ValintaperusteetValinnanVaiheDTO valinnanVaihe) {
        this.valinnanVaihe = valinnanVaihe;
    }

    public ValintaperusteetValinnanVaiheDTO getValinnanVaihe() {
        return valinnanVaihe;
    }

    public List<HakukohteenValintaperusteDTO> getHakukohteenValintaperuste() {
        return hakukohteenValintaperuste;
    }

    public void setHakukohteenValintaperuste(List<HakukohteenValintaperusteDTO> hakukohteenValintaperuste) {
        this.hakukohteenValintaperuste = hakukohteenValintaperuste;
    }

    public int getViimeinenValinnanvaihe() {
        return viimeinenValinnanvaihe;
    }

    public void setViimeinenValinnanvaihe(int viimeinenValinnanvaihe) {
        this.viimeinenValinnanvaihe = viimeinenValinnanvaihe;
    }
}
