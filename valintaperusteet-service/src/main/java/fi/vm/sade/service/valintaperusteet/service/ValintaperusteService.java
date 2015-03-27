package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakuparametritDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.CRUD;
import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.READ_UPDATE_CRUD;

/**
 * Created by jukais on 13.3.2014.
 */
public interface ValintaperusteService {
    List<ValintatapajonoDTO> haeValintatapajonotSijoittelulle(String hakukohdeOid);
    Map<String,List<ValintatapajonoDTO>> haeValintatapajonotSijoittelulle(Collection<String> hakukohdeOids);

    List<ValintaperusteetDTO> haeValintaperusteet(List<HakuparametritDTO> hakuparametrit);

    void tuoHakukohde(HakukohdeImportDTO hakukohde);
}
