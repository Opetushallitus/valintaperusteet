package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class UusiHakukohteenValintaperusteRekursio {

    private Long id;
    HakukohteenValintaperusteAvaimetDTO valintaperusteet;

    public UusiHakukohteenValintaperusteRekursio(Long id,
                                                 HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
        this.id = id;
        this.valintaperusteet = valintaperusteet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HakukohteenValintaperusteAvaimetDTO getValintaperusteet() {
        return valintaperusteet;
    }

    public void setValintaperusteet(HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
        this.valintaperusteet = valintaperusteet;
    }

}
