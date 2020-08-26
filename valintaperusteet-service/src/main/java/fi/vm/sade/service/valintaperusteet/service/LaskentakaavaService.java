package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViiteId;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface LaskentakaavaService {
  List<ValintaperusteDTO> findAvaimetForHakukohde(String hakukohdeOid);

  HakukohteenValintaperusteAvaimetDTO findHakukohteenAvaimet(String oid);

  List<Laskentakaava> findKaavas(
      boolean all,
      String valintaryhmaOid,
      String hakukohdeOid,
      fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi);

  Laskentakaava haeLaskettavaKaava(LaskentakaavaId id, Laskentamoodi laskentamoodi);

  Laskentakaava haeMallinnettuKaava(LaskentakaavaId id);

  Laskentakaava insert(LaskentakaavaInsertDTO laskentakaava);

  String haeHakuoid(String hakukohdeOid, String valintaryhmaOid);

  Pair<Laskentakaava, Laskentakaava> update(LaskentakaavaId id, LaskentakaavaCreateDTO laskentakaava);

  Laskentakaava siirra(LaskentakaavaSiirraDTO dto);

  LaskentakaavaId kopioiJosEiJoKopioitu(LaskentakaavaId lahdeLaskentakaava,
                                        HakukohdeViiteId kohdeHakukohde);

  LaskentakaavaId kopioiJosEiJoKopioitu(LaskentakaavaId lahdeLaskentakaava,
                                        ValintaryhmaId kohdeValintaryhma,
                                        Map<ValintaryhmaId, Map<LaskentakaavaId, LaskentakaavaId>> cache);


  List<LaskentakaavaId> irrotaHakukohteesta(HakukohdeViiteId hakukohdeViiteId);

  void liitaHakukohteeseen(HakukohdeViiteId hakukohdeViiteId, List<LaskentakaavaId> laskentakaavaIds);

  boolean poista(LaskentakaavaId id);
}
