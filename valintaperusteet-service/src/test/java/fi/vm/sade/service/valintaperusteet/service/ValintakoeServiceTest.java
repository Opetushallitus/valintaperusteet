package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiOleOlemassaException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: kwuoti Date: 15.4.2013 Time: 18.04 */
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeServiceTest extends WithSpringBoot {

  @Autowired private ValintakoeService valintakoeService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Test
  public void testLisaaValintakoeValinnanVaiheelle() {
    final String valinnanVaiheOid = "91";
    final String kopioValinnanVaiheOid = "92";

    {
      ValinnanVaihe vrValinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
      List<Valintakoe> vrValinnanVaihekokeet =
          valintakoeService.findValintakoeByValinnanVaihe(valinnanVaiheOid);
      assertEquals(1, vrValinnanVaihekokeet.size());

      ValinnanVaihe hkValinnanVaihe = valinnanVaiheService.readByOid(kopioValinnanVaiheOid);
      assertEquals(vrValinnanVaihe.getOid(), hkValinnanVaihe.getMaster().getOid());
      List<Valintakoe> hkValinnanVaiheKokeet =
          valintakoeService.findValintakoeByValinnanVaihe(hkValinnanVaihe.getOid());
      assertEquals(1, hkValinnanVaiheKokeet.size());
      assertEquals(
          vrValinnanVaihekokeet.get(0).getOid(), hkValinnanVaiheKokeet.get(0).getMaster().getOid());
    }

    final ValintakoeDTO valintakoe = new ValintakoeDTO();
    valintakoe.setAktiivinen(true);
    valintakoe.setKuvaus("uusikuvaus");
    valintakoe.setLaskentakaavaId(101L);
    valintakoe.setNimi("uusinimi");
    valintakoe.setTunniste("uusitunniste");
    valintakoe.setLahetetaankoKoekutsut(true);
    valintakoe.setKutsutaankoKaikki(false);
    valintakoe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    valintakoeService.lisaaValintakoeValinnanVaiheelle(valinnanVaiheOid, valintakoe);

    {
      Comparator<Valintakoe> valintakoeComparator =
          new Comparator<Valintakoe>() {
            @Override
            public int compare(Valintakoe o1, Valintakoe o2) {
              return o1.getId().compareTo(o2.getId());
            }
          };

      ValinnanVaihe vrValinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
      List<Valintakoe> vrValinnanVaihekokeet =
          valintakoeService.findValintakoeByValinnanVaihe(valinnanVaiheOid);
      Collections.sort(vrValinnanVaihekokeet, valintakoeComparator);
      assertEquals(2, vrValinnanVaihekokeet.size());

      ValinnanVaihe hkValinnanVaihe = valinnanVaiheService.readByOid(kopioValinnanVaiheOid);
      assertEquals(vrValinnanVaihe.getOid(), hkValinnanVaihe.getMaster().getOid());
      List<Valintakoe> hkValinnanVaiheKokeet =
          valintakoeService.findValintakoeByValinnanVaihe(hkValinnanVaihe.getOid());
      Collections.sort(hkValinnanVaiheKokeet, valintakoeComparator);
      assertEquals(2, hkValinnanVaiheKokeet.size());

      assertEquals(
          vrValinnanVaihekokeet.get(0).getOid(), hkValinnanVaiheKokeet.get(0).getMaster().getOid());
      assertEquals(
          vrValinnanVaihekokeet.get(1).getOid(), hkValinnanVaiheKokeet.get(1).getMaster().getOid());

      assertEquals(vrValinnanVaihekokeet.get(1).getAktiivinen(), valintakoe.getAktiivinen());
      assertEquals(vrValinnanVaihekokeet.get(1).getKuvaus(), valintakoe.getKuvaus());
      assertEquals(vrValinnanVaihekokeet.get(1).getNimi(), valintakoe.getNimi());
      assertEquals(vrValinnanVaihekokeet.get(1).getTunniste(), valintakoe.getTunniste());
      assertEquals(
          vrValinnanVaihekokeet.get(1).getLaskentakaava().getId(), valintakoe.getLaskentakaavaId());

      assertEquals(hkValinnanVaiheKokeet.get(1).getAktiivinen(), valintakoe.getAktiivinen());
      assertEquals(hkValinnanVaiheKokeet.get(1).getKuvaus(), valintakoe.getKuvaus());
      assertEquals(hkValinnanVaiheKokeet.get(1).getNimi(), valintakoe.getNimi());
      assertEquals(hkValinnanVaiheKokeet.get(1).getTunniste(), valintakoe.getTunniste());
      assertEquals(
          hkValinnanVaiheKokeet.get(1).getLaskentakaava().getId(), valintakoe.getLaskentakaavaId());
    }
  }

  @Test
  public void testDeleteByOid() {
    final String valintakoeOid = "oid8";
    final String kopioValintakoeOid = "oid9";

    valintakoeService.readByOid(valintakoeOid);
    Valintakoe kopio = valintakoeService.readByOid(kopioValintakoeOid);
    assertEquals(valintakoeOid, kopio.getMasterValintakoe().getOid());

    valintakoeService.delete(valintakoeOid);

    boolean caughtOne = false;
    try {
      valintakoeService.readByOid(valintakoeOid);
    } catch (ValintakoettaEiOleOlemassaException e) {
      caughtOne = true;
    }
    assertTrue(caughtOne);

    try {
      valintakoeService.readByOid(kopioValintakoeOid);
    } catch (ValintakoettaEiOleOlemassaException e) {
      caughtOne = true;
    }

    assertTrue(caughtOne);
  }

  @Test
  public void testUpdate() {
    final String valintakoeOid = "oid8";

    ValintakoeDTO update = new ValintakoeDTO();
    update.setAktiivinen(false);
    update.setKuvaus("kuvausta");
    update.setLaskentakaavaId(102L);
    update.setNimi("nimeäminen");
    update.setTunniste("uustunniste");
    update.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    Valintakoe managed = valintakoeService.readByOid(valintakoeOid);
    assertFalse(managed.getAktiivinen().equals(update.getAktiivinen()));
    assertFalse(managed.getKuvaus().equals(update.getKuvaus()));
    assertFalse(managed.getNimi().equals(update.getNimi()));
    assertFalse(managed.getTunniste().equals(update.getTunniste()));
    assertFalse(managed.getLaskentakaava().getId().equals(update.getLaskentakaavaId()));

    valintakoeService.update(valintakoeOid, update);

    managed = valintakoeService.readByOid(valintakoeOid);
    assertEquals(update.getAktiivinen(), managed.getAktiivinen());
    assertEquals(update.getKuvaus(), managed.getKuvaus());
    assertEquals(update.getNimi(), managed.getNimi());
    assertEquals(update.getTunniste(), managed.getTunniste());
    assertEquals(update.getLaskentakaavaId(), managed.getLaskentakaava().getId());

    Valintakoe kopio = valintakoeService.readByOid("oid9");
    assertEquals(update.getAktiivinen(), kopio.getAktiivinen());
    assertEquals(update.getKuvaus(), kopio.getKuvaus());
    assertEquals(update.getNimi(), kopio.getNimi());
    assertEquals(update.getTunniste(), kopio.getTunniste());
    assertEquals(update.getLaskentakaavaId(), kopio.getLaskentakaava().getId());
  }

  @Test(expected = FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException.class)
  public void testUpdateInvalidLaskentakaava() {
    final String valintakoeOid = "oid15";

    ValintakoeDTO update = new ValintakoeDTO();
    update.setAktiivinen(false);
    update.setKuvaus("kuvausta");
    update.setLaskentakaavaId(417L);
    update.setNimi("nimeäminen");
    update.setTunniste("uustunniste");

    valintakoeService.update(valintakoeOid, update);
  }
}
