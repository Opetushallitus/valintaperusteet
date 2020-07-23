package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache lookups for copying Valintaryhma entity hierarchies.
 *
 * <p>XXX: This cache is magical! When copying entity hierarchies the entries in these maps point
 * across the two detached hierarchies to ensure the new target hierarchy gets correct IDs. While
 * kind of nifty in function, this is really confusing and error prone if caching is not done for
 * all dependent child entities in the full hierarchy.
 */
public class JuureenKopiointiCache {

  public final Map<Long, Valintatapajono> kopioidutValintapajonot = new HashMap<>();
  public final Map<Long, Hakijaryhma> kopioidutHakijaryhmat = new HashMap<>();
  public final Map<Long, HakijaryhmaValintatapajono> kopioidutHakijaryhmaValintapajonot =
      new HashMap<>();
  public final Map<Long, ValinnanVaihe> kopioidutValinnanVaiheet = new HashMap<>();
  public final Map<Long, Jarjestyskriteeri> kopioidutJarjestyskriteerit = new HashMap<>();
  public final Map<Long, Valintakoe> kopioidutValintakokeet = new HashMap<>();
  public final Map<Long, Laskentakaava> kopioidutLaskentakaavat = new HashMap<>();
}
