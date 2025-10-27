package fi.vm.sade.service.valintaperusteet.config;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.spi.FlushEntityEvent;

/**
 * Custom-kuuntelija, jonka avulla on toteutettu serialisoitujen funktiokutsujen muutoksien
 * tunnistus. Vakiona Hibernate käyttää muutoksien tunnistamiseen equals-metodia, joka täytyisi siis
 * implementoida kaikille funktiokutsuihin liittyville luokille, mikä on ei-triviaali määrä koodia
 * ylläpitää testeineen (esim. puurakenne sisältää paljon Collectioneita joista osassa järjestys
 * merkkaa ja osassa ei). Nyt muutosten tunnistus toimii siten että {@link
 * Laskentakaava#setFunktiokutsu(Funktiokutsu)} -metodissa laitetaan päälle dirty-flagi, jota sitten
 * tutkitaan tässä kuuntelijassa. Mikäli flägiä ei ole asetettu, muutosta ei ole tapahtunut ja
 * dirty-tieto (vakiomekanismi toteaa funktiokutsun aina muuttuneeksi) poistetaan.
 */
public class CustomDefaultFlushEventEntityListener extends DefaultFlushEntityEventListener {

  @Override
  protected void dirtyCheck(FlushEntityEvent event) throws HibernateException {
    super.dirtyCheck(event);
    laskentakaavaDirtyCheck(event);
  }

  static void laskentakaavaDirtyCheck(FlushEntityEvent event) {
    if (!(event.getEntity() instanceof Laskentakaava)) {
      return;
    }
    for (int i = 0; i < event.getPropertyValues().length; i++) {
      if (event.getPropertyValues()[i] instanceof Laskentakaava.FunktiokutsuWrapper) {
        Laskentakaava.FunktiokutsuWrapper wrapper =
            (Laskentakaava.FunktiokutsuWrapper) event.getPropertyValues()[i];
        if (!wrapper.isDirty()) {
          event.setDirtyProperties(ArrayUtils.removeAllOccurrences(event.getDirtyProperties(), i));
        }
      }
    }
  }
}
