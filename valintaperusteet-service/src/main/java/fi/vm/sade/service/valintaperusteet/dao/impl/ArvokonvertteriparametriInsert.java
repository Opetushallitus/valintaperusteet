package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dto.ArvokonvertteriparametriDTO;
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma;

public class ArvokonvertteriparametriInsert {
    public final long funktiokutsuId;
    public final TekstiRyhma tekstiryhma;
    public final ArvokonvertteriparametriDTO dto;

    public ArvokonvertteriparametriInsert(long funktiokutsuId,
                                          TekstiRyhma tekstiryhma,
                                          ArvokonvertteriparametriDTO dto) {
        this.funktiokutsuId = funktiokutsuId;
        this.tekstiryhma = tekstiryhma;
        this.dto = dto;
    }
}
