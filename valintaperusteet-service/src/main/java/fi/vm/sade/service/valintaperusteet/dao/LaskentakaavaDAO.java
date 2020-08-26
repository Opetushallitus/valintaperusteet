package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViiteId;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.ValintaryhmaId;

import java.util.List;

public interface LaskentakaavaDAO {
  Laskentakaava insert(LaskentakaavaCreateDTO dto,
                       LaskentakaavaId kopioLaskentakaavasta,
                       ValintaryhmaId valintaryhma,
                       HakukohdeViiteId hakukohdeViite);

  Laskentakaava read(LaskentakaavaId id);

  Laskentakaava update(LaskentakaavaId id, LaskentakaavaCreateDTO dto);

  boolean delete(LaskentakaavaId id);

  List<Laskentakaava> findKaavas(
      boolean all,
      String valintaryhmaOid,
      String hakukohdeOid,
      fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi);

  List<Laskentakaava> findHakukohteenKaavat(String hakukohdeOid);

  Laskentakaava etsiKaavaTaiSenKopio(LaskentakaavaId laskentakaavaId,
                                     HakukohdeViiteId hakukohdeViiteId,
                                     ValintaryhmaId valintaryhmaId);

  List<LaskentakaavaId> irrotaHakukohteesta(HakukohdeViiteId hakukohdeViiteId);

  void liitaHakukohteeseen(HakukohdeViiteId hakukohdeViiteId, List<LaskentakaavaId> laskentakaavaIds);
}
