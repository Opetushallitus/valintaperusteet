package fi.vm.sade.service.valintaperusteet.model;

import jakarta.persistence.EntityManager;

/**
 * Apuluokka jonka avulla saadaan jpa entitymanageri laskentakaavan funktiokutsun
 * jsonb-serialisoinnin käyttöön. Tätä tarvitaan koska funktiokutsun viitatessa toiseen
 * laskentakaavaan serialisoinnissa tallennetaan vain laskentakaavan tunniste, ja deserialisoinnissa
 * haetaan taas entitymanagerin avulla itse laskentakaava.
 */
public class EntityManagerUtils {

  private static EntityManager em;

  public static void setEntityManager(EntityManager em) {
    EntityManagerUtils.em = em;
  }

  public static EntityManager getEntityManager() {
    return em;
  }
}
