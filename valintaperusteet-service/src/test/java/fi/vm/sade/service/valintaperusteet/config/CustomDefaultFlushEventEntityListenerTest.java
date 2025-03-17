package fi.vm.sade.service.valintaperusteet.config;

import static org.junit.jupiter.api.Assertions.*;

import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import org.hibernate.event.spi.FlushEntityEvent;
import org.junit.jupiter.api.Test;

public class CustomDefaultFlushEventEntityListenerTest {

  @Test
  public void testLaskentakaavaDirtyCheck_notAnInstanceOfLaskentakaava() {
    // jos entiteetti ei ole Laskentakaava
    int dirtyProperties[] = new int[] {1, 2, 3};
    FlushEntityEvent event = new FlushEntityEvent(null, new Object(), null);
    event.setDirtyProperties(dirtyProperties);

    // dirty-propertyjä ei muuteta
    CustomDefaultFlushEventEntityListener.laskentakaavaDirtyCheck(event);
    assertArrayEquals(dirtyProperties, event.getDirtyProperties());
  }

  @Test
  public void testLaskentakaavaDirtyCheck_InstanceofLaskentakaava_NotDirty() {
    // jos entiteetti on Laskentakaava, funktiokutsu-kenttä (indeksi 1) lähtökohtaisesti dirty
    int dirtyProperties[] = new int[] {1, 2, 3};
    FlushEntityEvent event = new FlushEntityEvent(null, new Laskentakaava(), null);
    event.setDirtyProperties(dirtyProperties);
    event.setPropertyValues(
        new Object[] {"abc", new Laskentakaava.FunktiokutsuWrapper(null, false), "def"});

    // funktiokutsu-kenttä (indeksi 2) ei enää dirty-indeksien joukossa
    CustomDefaultFlushEventEntityListener.laskentakaavaDirtyCheck(event);
    assertArrayEquals(new int[] {2, 3}, event.getDirtyProperties());
  }

  @Test
  public void testLaskentakaavaDirtyCheck_InstanceofLaskentakaava_IsDirty() {
    // jos entiteetti on Laskentakaava, funktiokutsu-kenttä (indeksi 1) lähtökohtaisesti dirty
    int dirtyProperties[] = new int[] {1, 2, 3};
    FlushEntityEvent event = new FlushEntityEvent(null, new Laskentakaava(), null);
    event.setDirtyProperties(dirtyProperties);
    event.setPropertyValues(
        new Object[] {"abc", new Laskentakaava.FunktiokutsuWrapper(null, true), "def"});

    // funktiokutsu-kenttä (indeksi 2) edelleen dirty-indeksien joukossa
    CustomDefaultFlushEventEntityListener.laskentakaavaDirtyCheck(event);
    assertArrayEquals(dirtyProperties, event.getDirtyProperties());
  }
}
