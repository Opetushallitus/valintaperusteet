package fi.vm.sade.service.valintaperusteet.service.impl.util.koodi;

import fi.vm.sade.service.valintaperusteet.dao.KoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Koodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import java.util.*;

public abstract class KoodiHandler<T extends Koodi> {
  private ValintaryhmaService valintaryhmaService;
  private KoodiDAO<T> koodiDAO;

  public KoodiHandler(ValintaryhmaService valintaryhmaService, KoodiDAO<T> koodiDAO) {
    this.valintaryhmaService = valintaryhmaService;
    this.koodiDAO = koodiDAO;
  }

  public void lisaaKoodiValintaryhmalle(String valintaryhmaOid, T koodi) {
    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    T haettu = koodiDAO.readByUri(koodi.getUri());
    if (haettu != null) {
      haettu.setUri(koodi.getUri());
      haettu.setNimiFi(koodi.getNimiFi());
      haettu.setNimiSv(koodi.getNimiSv());
      haettu.setNimiEn(koodi.getNimiEn());
      haettu.setArvo(koodi.getArvo());
    } else {
      haettu = koodiDAO.insertOrUpdate(koodi);
    }
    addKoodiToValintaryhma(valintaryhma, haettu);
  }

  public void paivitaValintaryhmanKoodit(String valintaryhmaOid, Collection<T> koodit) {
    Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
    clearValintaryhmaKoodis(valintaryhma);
    Map<String, T> urit = new HashMap<String, T>();
    Map<String, Integer> esiintymat = new HashMap<String, Integer>();
    if (koodit == null) {
      koodit = new ArrayList<T>();
    }
    for (T k : koodit) {
      if (!urit.containsKey(k.getUri())) {
        urit.put(k.getUri(), k);
        esiintymat.put(k.getUri(), 1);
      } else {
        Integer lkm = esiintymat.get(k.getUri()) + 1;
        esiintymat.put(k.getUri(), lkm);
      }
    }
    List<T> managedKoodis =
        koodiDAO.findByUris(urit.keySet().toArray(new String[urit.keySet().size()]));
    for (T managed : managedKoodis) {
      if (urit.containsKey(managed.getUri())) {
        for (int i = 0; i < esiintymat.get(managed.getUri()); ++i) {
          addKoodiToValintaryhma(valintaryhma, managed);
        }
        urit.remove(managed.getUri());
        esiintymat.remove(managed.getUri());
      }
    }
    for (T uusiKoodi : urit.values()) {
      T lisatty = koodiDAO.insert(uusiKoodi);
      for (int i = 0; i < esiintymat.get(uusiKoodi.getUri()); ++i) {
        addKoodiToValintaryhma(valintaryhma, lisatty);
      }
    }
  }

  protected abstract void clearValintaryhmaKoodis(Valintaryhma valintaryhma);

  protected abstract void addKoodiToValintaryhma(Valintaryhma valintaryhma, T koodi);
}
