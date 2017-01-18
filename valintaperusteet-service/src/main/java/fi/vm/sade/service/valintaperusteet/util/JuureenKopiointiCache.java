package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

import java.util.HashMap;
import java.util.Map;

public class JuureenKopiointiCache {

    public final Map<Long, Valintatapajono> kopioidutValintapajonot = new HashMap<>();
    public final Map<Long, ValinnanVaihe> kopioidutValinnanVaiheet = new HashMap<>();
}
