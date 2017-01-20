package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.*;

import java.util.HashMap;
import java.util.Map;

public class JuureenKopiointiCache {

    public final Map<Long, Valintatapajono> kopioidutValintapajonot = new HashMap<>();
    public final Map<Long, Hakijaryhma> kopioidutHakijaryhmat = new HashMap<>();
    public final Map<Long, HakijaryhmaValintatapajono> kopioidutHakijaryhmaValintapajonot = new HashMap<>();
    public final Map<Long, ValinnanVaihe> kopioidutValinnanVaiheet = new HashMap<>();
    public final Map<Long, Jarjestyskriteeri> kopioidutJarjestyskriteerit = new HashMap<>();
}
