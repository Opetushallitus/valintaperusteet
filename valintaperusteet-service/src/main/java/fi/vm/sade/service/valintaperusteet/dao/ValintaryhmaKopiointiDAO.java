package fi.vm.sade.service.valintaperusteet.dao;

/**
 * Valintaryhmän alipuun kopiointi joukko-operaatioina tietokannassa.
 *
 * <p>Kopiointi tehdään kokonaan SQL:llä, jolloin sovelluksen muistinkäyttö ei riipu kopioitavan
 * alipuun koosta. Toteutus varaa ensin kaikille kopioitaville riveille uudet tunnisteet
 * väliaikaiseen mäppäystauluun ja tekee sen jälkeen jokaiselle taululle yhden {@code INSERT ...
 * SELECT} -lauseen, jossa viitteet ratkaistaan mäppäyksen kautta. Viitteitä ei voi jättää
 * jälkikäteen päivitettäviksi, koska skeemassa on osittaisia uniikki-indeksejä (esim. {@code
 * valinnan_vaihe_valintaryhma_ensimmainen_key}), jotka tarkistetaan rivikohtaisesti.
 */
public interface ValintaryhmaKopiointiDAO {

  /**
   * Kopioi lähdevalintaryhmän alipuun uudeksi juurivalintaryhmäksi. Kopioon tulevat lähteen omat
   * valinnan vaiheet, jonot, järjestyskriteerit, valintakokeet, hakijaryhmät ja laskentakaavat, ja
   * niiden keskinäiset master- ja edellinen-viitteet osoitetaan kopioihin.
   *
   * @return kopioidun juuren oid
   */
  String kopioiJuureen(String lahdeOid, String nimi);

  /**
   * Kopioi lähdevalintaryhmän alipuun olemassa olevan valintaryhmän alle. Kopio perii rakenteensa
   * kohdevanhemmalta: valinnan vaiheet, jonot, järjestyskriteerit, valintakokeet ja hakijaryhmät
   * kloonataan vanhemmalta master-viitteineen, kun taas laskentakaavat ja koodit tulevat
   * lähdevalintaryhmästä.
   *
   * @return kopioidun juuren oid
   */
  String kopioiVanhemmanAlle(String lahdeOid, String vanhempiOid, String nimi);
}
