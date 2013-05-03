package fi.vm.sade.service.valintaperusteet.service;

import java.util.List;

public interface PaasykoeTunnisteetService {

    List<String> haeTunnisteetHakukohteelle(String hakukohdeoid);
}
