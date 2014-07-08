package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.37
 */
public interface HakukohdeImportService {
    void tuoHakukohde(HakukohdeImportTyyppi hakukohde);

    void tuoHakukohdeRest(HakukohdeImportDTO hakukohde);
}
