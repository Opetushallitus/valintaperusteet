package fi.vm.sade.service.valintaperusteet.service.impl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import org.junit.jupiter.api.Test;

public class ObjectGraphUtilTest {

  @Test
  public void testExtractObjectsOfType_Basecase() {
    // stringi löytyy syvältä objektipuun kätköistä
    Object objectGraph = Set.of(Map.of(1, "abc"));
    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals("abc", result.iterator().next());
  }

  @Test
  public void testExtractObjectsOfType_EmptyGraph() {
    // on ok ettei löydetä mitään
    ArrayList<Object> objectGraph = new ArrayList<>();
    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals(0, result.size());
  }

  static class BizClazz {
    Object field;
  }

  @Test
  public void testExtractObjectsOfType_Cyclic() {
    // jos objektit muodostavat syklejä se on ok
    BizClazz obj = new BizClazz();
    Object objectGraph = Set.of(Map.of(1, obj));
    obj.field = objectGraph;

    Collection<String> result = ObjectGraphUtil.extractObjectsOfType(objectGraph, String.class);
    assertEquals(0, result.size());
  }
}
