package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;

import java.util.Map;

public class UusiHakukohteenValintaperusteRekursio {
    private Long id;
    HakukohteenValintaperusteAvaimetDTO valintaperusteet;

    public UusiHakukohteenValintaperusteRekursio(Long id, HakukohteenValintaperusteAvaimetDTO valintaperusteet) {
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
