package fi.vm.sade.service.valintaperusteet.service.impl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import org.junit.jupiter.api.Test;

public class ObjectGraphUtilTest {

  @Test
  public void testExtractObjectsOfType_Perustapaus() {
    // stringi löytyy syvältä objektipuun kätköistä
    Object objectGraph = Set.of(Map.of(1, "abc"));
    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals("abc", result.iterator().next());
  }

  @Test
  public void testExtractObjectsOfType_Tyhjagraafi() {
    // tyhjä objektigraafi on ok
    ArrayList<Object> objectGraph = null;
    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals(0, result.size());
  }

  @Test
  public void testExtractObjectsOfType_EiTyypinInstansseja() {
    // on ok ettei löydetä kyseisen tyypin instansseja
    Object objectGraph = Set.of(Map.of(1, 2));
    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals(0, result.size());
  }

  static class BizClazz {
    Object field;
  }

  @Test
  public void testExtractObjectsOfType_Syklinen() {
    // jos objektit muodostavat syklejä se on ok
    BizClazz obj = new BizClazz();
    Object objectGraph = Set.of(Map.of(1, obj));
    obj.field = objectGraph;

    Collection<BizClazz> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, BizClazz.class);
    assertEquals(1, result.size());
  }
}
